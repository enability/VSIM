#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <vector>
#include <cmath>
#include <ctime>

#define PI 3.14159265
#define MIN(x,y) ((x)<(y))?(x):(y) 
#define MAX(x,y) ((x)>(y))?(x):(y) 
#define MAX_FLOATERS 20
#define FLOATER_MAX_OPACITY 200
#define MAX_RINGS 10

//#define DEBUG
/*
    Do the image processing here.
    Get addresses to matrices and do it.
    References
    Accessing individual pixels -  http://docs.opencv.org/doc/tutorials/core/how_to_scan_images/how_to_scan_images.html    
*/


using namespace std;
using namespace cv;

extern "C" {
JNIEXPORT void JNICALL Java_com_enability_visionsimulator_cataract_FindFeaturesCataract(JNIEnv *env, jobject thisObj, jlong addrRgba, jlong addrDarkveil, jint viewMode, jint showRings, jint enableMacularEdema, jint enableMobilityMode, jint enableContrastSensitivity, jintArray inblurValues, jfloatArray inblurRadii, jint callInit, jint enableDebug);
// Custom classes
class Floater
 {
 public:
 Point centre;
 Size size;
 uchar opacity;
 unsigned int enabled;
 Floater() ;
 ~Floater(){} ;
 void update(void);
 }; 
 
 Floater::Floater(){
    centre = Point(0,0);
    size = Size(15,15);
    opacity = 0;
    enabled = 0;
 }
 void Floater::update(void)
 {
    unsigned int opacity_ = opacity;
    opacity = (opacity_+20)%FLOATER_MAX_OPACITY;
    // move randomly
    
 }
 Floater floaters[MAX_FLOATERS];
 
// Globals
int initReady = 0;
unsigned long nframe = 0;
// LUT for blur
Mat blurLUT;
IplImage img_blurLUT;
int step_blurLUT, channels_blurLUT;
uchar *data_blurLUT;

JNIEXPORT void JNICALL Java_com_enability_visionsimulator_cataract_FindFeaturesCataract(JNIEnv *env, jobject thisObj, jlong addrRgba, jlong addrDarkveil, jint viewMode, jint showRings, jint enableMacularEdema, jint enableMobilityMode, jint enableContrastSensitivity, jintArray inblurValues, jfloatArray inblurRadii, jint callInit, jint enableDebug)
{    
    // Convert the incoming JNI types to C types
    // Step 1: Convert the incoming JNI jintarray to C's jint[]
    // http://www.ntu.edu.sg/home/ehchua/programming/java/JavaNativeInterface.html#zz-4.3
	// with modifications    
    int *blurValues = env->GetIntArrayElements(inblurValues,NULL);
    float *blurRadii = env->GetFloatArrayElements(inblurRadii, NULL);
    int length_blurValues = env->GetArrayLength(inblurValues);
    
    Mat& mRgb = *(Mat*)addrRgba;
    Mat& mDarkveil = *(Mat*)addrDarkveil;
  // Key parameters  
  float ringR[MAX_RINGS];// = {0.0, 0.1, 0.3, 0.55, 0.9}; //{0.0, 0.119, 0.239, 0.469, 0.9};
  int blurSizes[MAX_RINGS];// = {1,3,5,7,9};
  int num_floaters = MAX_FLOATERS;  

  // Copy parameters
  int numBlurRegions;
  
  // Parameters end
  
  // Update globals
  nframe++;

  // Read basic data about frame
  IplImage img = mRgb.operator IplImage();
  int height    = img.height;
  int width     = img.width;
  int step      = img.widthStep;
  int channels  = img.nChannels;
  uchar *data      = (uchar *)img.imageData;

  int i,j,k;

  Mat mtmp, mRes ;
  
  mRgb.copyTo(mtmp);
  mRgb.copyTo(mRes);

  IplImage img_tmp = mtmp.operator IplImage();
  uchar *data_tmp = (uchar *)img_tmp.imageData;

  IplImage img_mRes = mRes.operator IplImage();
  uchar *data_mRes = (uchar *)img_mRes.imageData;

  
  int halfHeight = height / 2;
  int halfWidth = width / 2;
  int rsqr;
  int Rmax = halfHeight;
  //int R1, R2, R1sqr, R2sqr;
  float bsqr = halfWidth*halfWidth;
  float asqr = halfHeight*halfHeight;
  float absqr = asqr * bsqr;
  float dsqr, D1, D2, D1sqr, D2sqr;
  
  int blurSize;
  unsigned char count;
    
  int k_blurLUT;
  if (enableDebug == 0)
  {
    numBlurRegions = 5;
   // float defaultringR[] = {0.0, 0.1, 0.3, 0.55, 0.9};
    //for (int i = 0; i < 5; i++) ringR[i] = defaultringR[i];
    
    switch (viewMode)
    {
        //int blurSizes[] = {1,3,5,7,9};
        case 0:
            //blurSizes[] = {1,3,5,7,9};
            blurSizes[0] = 1;
            blurSizes[1] = 4;
            blurSizes[2] = 9;
            blurSizes[3] = 14;
            blurSizes[4] = 20;
        break;
        case 1:
            //blurSizes[] = {3,3,5,7,9};
            blurSizes[0] = 4;
            blurSizes[1] = 4;
            blurSizes[2] = 9;
            blurSizes[3] = 14;
            blurSizes[4] = 20;            
            break;
        case 2:
            //blurSizes[] = {5,5,5,7,9};
            blurSizes[0] = 12;
            blurSizes[1] = 4;
            blurSizes[2] = 9;
            blurSizes[3] = 14;
            blurSizes[4] = 20;                        
            break;            
        case 3:            
            //blurSizes[] = {7,6,5,7,9};
            blurSizes[0] = 12;
            blurSizes[1] = 5;
            blurSizes[2] = 10;
            blurSizes[3] = 14;
            blurSizes[4] = 20;                        
            break;
        case 4:            
            //blurSizes[] = {7,6,5,7,9};
            blurSizes[0] = 13;
            blurSizes[1] = 6;
            blurSizes[2] = 11;
            blurSizes[3] = 15;
            blurSizes[4] = 22;
            break;
        default:
            return;
        break;
    }
   } 
   else
   {
      numBlurRegions = length_blurValues;
      for (int i = 0; i < length_blurValues; i++) 
      {
        blurSizes[i] = blurValues[i];
        ringR[i] = blurRadii[i];
      }
   
   }

    switch (viewMode)
    {
        case 0:
            num_floaters = 0;
        break;
        case 1:
            num_floaters = 0; 
            break;
        case 2:
            num_floaters = 0;
            break;            
        case 3:            
            num_floaters = MAX_FLOATERS;
            break;
        case 4:            
            num_floaters = 0;
            break;
        default:
            return;
        break;
    }
    
  // Initialize floaters, foveal blur LUT
  if (initReady == 0 || callInit > 0)
  {
    // Crop the darkveil
    //mRgb.copyTo(Darkveil);
    //Darkveil = mDarkveil(Rect(0,0,height,width));
    // Create a few floaters    
    srand(time(NULL));
    for (int i = 0; i < MAX_FLOATERS; i++)
    {
        int x = (int) (rand()%width);
        int y = (int) (rand()%height);    
        floaters[i].centre = Point(x,y);
        if (i < num_floaters)
            floaters[i].enabled = 1;    
        else
            floaters[i].enabled = 0;    
        floaters[i].opacity = (unsigned int)(rand()%FLOATER_MAX_OPACITY);
    }
    
    
    // Generate the LUT used for vision field blur
    if (initReady == 0)
    {
		blurLUT  = Mat::zeros(mRgb.rows, mRgb.cols, CV_8UC1);
		img_blurLUT = blurLUT.operator IplImage();
		step_blurLUT      = img_blurLUT.widthStep;
		channels_blurLUT  = img_blurLUT.nChannels;
		data_blurLUT = (uchar *)img_blurLUT.imageData;
    }
    
    // LUT
    // 0 = innermost
    // 1 = next
    // blurRings = second from inner ...
  for (count = 0; count < numBlurRegions; count++)
  {
    blurSize = blurSizes[count];
    
    D1sqr = ringR[count]*absqr;
    if (count < numBlurRegions - 1)
    {
        D2sqr = ringR[count+1]*absqr;
        // Label points in the radius range R1 < r < R2
        // using count
        
          for(i=0;i<height;i++)
          {
            for(j=0;j<width;j++)
            {
                dsqr = bsqr*(i-halfHeight)*(i-halfHeight) + asqr*(j-halfWidth)*(j-halfWidth);
                if ( dsqr > D1sqr && dsqr < D2sqr )
                {
                    k_blurLUT = i*step_blurLUT+j*channels_blurLUT;
                //    data_blurLUT[k_blurLUT+0] = (uchar) (count);
                }
            }
          }
        
        }
     else
     {
          for(i=0;i<height;i++)
          {
            for(j=0;j<width;j++)
            {
                dsqr = bsqr*(i-halfHeight)*(i-halfHeight) + asqr*(j-halfWidth)*(j-halfWidth);
                if ( dsqr > D1sqr )
                {
                    k_blurLUT = i*step_blurLUT+j*channels_blurLUT;
                   // data_blurLUT[k_blurLUT+0] = (uchar) (count);
                }
            }
          }
     
     }
    }

    initReady = 1;
    //return;
  }
    
  // View mode
    for (int i = 0; i < MAX_FLOATERS; i++)
    {
        if (i < num_floaters)
            floaters[i].enabled = 1;    
        else
            floaters[i].enabled = 0;    
    
    }
  
  
  // Divide Rmax into numBlurRegions
  // R1 = 0, x, 2x, 3x
  // R2 = x, 2x, 3x, 4x
  // Ellipse bsqr*xsqr + asqr*/ysqr - asqr*bsqr = 0
  
for (count = 0; count < numBlurRegions; count++)
{
    blurSize = blurSizes[count];
    if (enableDebug == 1)
    {
        blur( mRgb, mtmp, Size( blurSize, blurSize ) );
    }
    else
    {
        if (count == 0)
        {
            if (enableMacularEdema == 1)
            {
                blur( mRgb, mtmp, Size( blurSize, blurSize ) );                
            }
            else
            {
                // Skip it
                continue;
            }
        }
            // skip  2, 3
        if (count == 2 || count == 3)
        {
            if (enableMobilityMode == 1)
            {
                continue;
            }
            else
            {
                blur( mRgb, mtmp, Size( blurSize, blurSize ) );
            }
        }
        if (count > 3)
        {
            blur( mRgb, mtmp, Size( blurSize, blurSize ) );
        }
        
    }
    
    D1sqr = ringR[count]*absqr;
    D2sqr = ringR[count+1]*absqr;

    // Copy things in the radius range R1 < r < R2
    uchar blurLUT_label = count;
    
    for(i=0;i<height;i++)
    {
        for(j=0;j<width;j++)
        {
            k_blurLUT = i*step_blurLUT+j*channels_blurLUT;
            if (data_blurLUT[k_blurLUT+0] == blurLUT_label)
            {
                k = i*step+j*channels;
                data_mRes[k+0] = (uchar) data_tmp[k+0];
                data_mRes[k+1] = (uchar) data_tmp[k+1];
                data_mRes[k+2] = (uchar) data_tmp[k+2];
                if (showRings == 1)
                {
                    data_mRes[k+(count%3)] = 255;
                }
            }
        }
    }
    
}
    
  
    // Draw floaters
    // Threshold image to get regions to draw floaters
    // Take a grayscale image and draw on it
    // Then, set channels to high value whereever the mask if high
    Mat mfMask;
    //mRgb.convertTo(mfMask,CV_8UC1);
    mfMask = Mat::zeros(mRgb.rows, mRgb.cols, CV_8UC1);
    
    IplImage img_mfMask = mfMask.operator IplImage();
    int step2      = img_mfMask.widthStep;
    int channels2  = img_mfMask.nChannels;
    
    uchar *data_mfMask = (uchar *)img_mfMask.imageData;
    
    int kmask;
    
    // Draw on this
    int floaterId, idx;    
    for (idx = 0; idx < MAX_FLOATERS; idx++)
    {
        if (floaters[idx].enabled)
        {
            floaters[idx].update();
            Point centre = floaters[idx].centre;
            Size size = floaters[idx].size;
            floaterId = idx+1;
            ellipse(mfMask,centre,size,0,0,360,floaterId,-1);
        }
    }
    // Apply fMask
    uchar maskVal;

      for(i=0;i<height;i++)
      {
        for(j=0;j<width;j++)
        {
                k = i*step+j*channels;
                kmask = i*step2+j*channels2;             
                maskVal = data_mfMask[kmask];
                if (maskVal > 0 && maskVal < MAX_FLOATERS) // Floaters
                { 
                    floaterId = maskVal;
                    idx = floaterId - 1;
                    data_mRes[k+0] = (uchar) MAX(floaters[idx].opacity,data_mRes[k+0]);
                    //data_mRes[k+1] = (uchar) data_tmp[k+1];
                    //data_mRes[k+2] = (uchar) data_tmp[k+2];                
                }
        }
      }

    // Apply the cloud    
    IplImage img_Darkveil = mDarkveil.operator IplImage();
    int step3      = img_Darkveil.widthStep;
    int channels3  = img_Darkveil.nChannels;
    
    uchar *data_Darkveil = (uchar *)img_Darkveil.imageData;
    
    int kveil;
    unsigned int x;
    
    //Darkveil
    if (viewMode == 4)
    {
      for(i=0;i<height;i++)
      {
        for(j=0;j<width;j++)
        {
                k = i*step+j*channels;
                kveil = i*step3+j*channels3;
                x = (data_mRes[k+0] * data_Darkveil[kveil+0]);
                data_mRes[k+0] = (uchar) (x>>8);
                x = (data_mRes[k+1] * data_Darkveil[kveil+1]);
                data_mRes[k+1] = (uchar) (x>>8);
                x = (data_mRes[k+2] * data_Darkveil[kveil+2]);
                data_mRes[k+2] = (uchar) (x>>8);
                
        }
      }
    }

    // Blue Yellow deficiency
    // http://www.inf.ufrgs.br/~oliveira/pubs_files/CVD_Simulation/CVD_Simulation.html
    float r,g,b, r2, g2, b2;
    float R1[] = {1.255528,-0.076749,-0.178779};
    float R2[] = {-0.078411,0.930809,0.147602};
    float R3[] = {0.004733,0.691367,0.303900};  

    if (viewMode >= 2)
    {
      for(i=0;i<height;i++)
      {
        for(j=0;j<width;j++)
        {
            k = i*step+j*channels;
            r = (float) data_mRes[k+0];          
            g = (float) data_mRes[k+1];          
            b = (float) data_mRes[k+2];          
            r2 = R1[0]*r + R1[1]*g + R1[2]*b;
            g2 = R2[0]*r + R2[1]*g + R2[2]*b;
            b2 = R3[0]*r + R3[1]*g + R3[2]*b;
//            data_mRes[k+0] = (uchar) r2;
	    data_mRes[k+0] = 0;
            data_mRes[k+1] = (uchar) g2;
            data_mRes[k+2] = (uchar) b2;                
        }
      }
    }

if (viewMode == 1)
    {
      for(i=0;i<height;i++)
      {
        for(j=0;j<width;j++)
        {
            k = i*step+j*channels;
            r = (float) data_mRes[k+0];          
            g = (float) data_mRes[k+1];          
            b = (float) data_mRes[k+2];          
            r2 = R1[0]*r + R1[1]*g + R1[2]*b;
            g2 = R2[0]*r + R2[1]*g + R2[2]*b;
            b2 = R3[0]*r + R3[1]*g + R3[2]*b;
            //data_mRes[k+0] = (uchar) r2;
	    data_mRes[k+0] = (uchar) r2;
            data_mRes[k+1] = (uchar) g2;
            data_mRes[k+2] = (uchar) b2;  
			
        }
      }
    }

    
    // end
    mRes.copyTo(mRgb);
}
}
