import java.io.File;
import java.io.IOException;
import java.nio.Buffer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Pixelizer {
    BufferedImage ogImage;

    public void Pixelizer(){

    }
    /*
    This method reads in a file and then creates and assigns a BufferedImage object to be used later
    */
    public void imageReader(String path){
        try {
            File inputFile = new File(path);
            BufferedImage image = ImageIO.read(inputFile);
            this.ogImage=image;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*This method reduces the current image's resolution by incrementing pixel by pixel, calculating the average of each color for the given square, and then
    assigning the average colors to a new smaller bufferedImage object*/
    public void reduceResolution(int stepSize){
        BufferedImage newImage = new BufferedImage(this.ogImage.getWidth()/stepSize, this.ogImage.getHeight()/stepSize, BufferedImage.TYPE_INT_RGB);
        for(int y=stepSize-1;y<this.ogImage.getHeight();y+=stepSize){
            for(int x=stepSize-1;x<this.ogImage.getWidth();x+=stepSize){ 
                int a = 0;
                int r = 0;
                int g = 0;
                int b = 0;
                for(int i=0;i<stepSize;i++){
                    for(int j=0;j<stepSize;j++){
                        int pixelValue = this.ogImage.getRGB(x, y);
                        a+=(pixelValue >> 24) & 0xFF;
                        r+=(pixelValue >> 16) & 0xFF;
                        g+=(pixelValue >> 8) & 0xFF;
                        b+=pixelValue & 0xFF;
                    }
                }
                a=a/(stepSize*stepSize);
                r=r/(stepSize*stepSize);
                g=g/(stepSize*stepSize);
                b=b/(stepSize*stepSize);
                int rgbValue = (a << 24) | (r << 16) | (g << 8) | b;
                newImage.setRGB(x/(stepSize), y/(stepSize), rgbValue);
            }
        }  
        this.ogImage=newImage;
    }

    /*
    This method writes the current BufferedImage object to a png file
     */
    public void imageToPNG(){
        try{
            File outputFile = new File("output.png");
            ImageIO.write(this.ogImage,"PNG",outputFile);

        }catch(IOException e){
            System.out.println("error saving to file");
        }
    }

    /*
    This method converts the ogImage to greyscale by averaging all colors of a given pixel, and then assigning each rgb value to the average
    */
    public void toGreyscale(){
        for(int y=0;y<this.ogImage.getHeight();y+=1){
            for(int x=0;x<this.ogImage.getWidth();x+=1){ 
                int pixelValue = this.ogImage.getRGB(x, y);
                int a=(pixelValue >> 24) & 0xFF;
                int r=(pixelValue >> 16) & 0xFF;
                int g=(pixelValue >> 8) & 0xFF;
                int b=pixelValue & 0xFF;

                int averageRGB=(r+g+b)/3;
                a=averageRGB;
                r=averageRGB;
                g=averageRGB;
                b=averageRGB;
                int rgbValue = (a << 24) | (r << 16) | (g << 8) | b;
                this.ogImage.setRGB(x, y, rgbValue);
            }
        }
    }

    
    public void toBlackAndWhite(double threshold){
        this.toGreyscale();
        for(int y=0;y<this.ogImage.getHeight();y+=1){
            for(int x=0;x<this.ogImage.getWidth();x+=1){ 
                this.pixelThreshold(x, y, threshold);
            }
        }
    }

    public void bayerDither(){
        for(int y=0;y<this.ogImage.getHeight();y+=2){
            for(int x=0;x<this.ogImage.getWidth();x+=2){
                this.pixelThreshold(x, y, 0);
                this.pixelThreshold(x+1, y, 0.5);
                this.pixelThreshold(x, y+1, 0.75);
                this.pixelThreshold(x+1, y+1, 0.25);
            }
        }
    }

    public void pixelThreshold(int x, int y, double threshold){
        int r=(this.ogImage.getRGB(x, y) >> 16) & 0xFF;
        int g=(this.ogImage.getRGB(x, y) >> 8) & 0xFF;
        int b=this.ogImage.getRGB(x, y) & 0xFF;
        if(r>threshold*255){
            r=255;
        }else{
            r=0;
        }

        if(g>threshold*255){
            g=255;
        }else{
            g=0;
        }

        if(b>threshold*255){
            b=255;
        }else{
            b=0;
        }

        this.ogImage.setRGB(x,y,((255 << 24) | (r << 16) | (g << 8) | b));
    }
    public static void main(String[] args){
        Pixelizer pixelizer=new Pixelizer();
        pixelizer.imageReader("testImage.jpg");
        //BufferedImage image =pixelizer.reduceResolution(Integer.parseInt(args[0]));
        //pixelizer.toGreyscale();
        //pixelizer.toBlackAndWhite(Double.parseDouble(args[1]));
        pixelizer.bayerDither();
        pixelizer.imageToPNG();
    }
}
