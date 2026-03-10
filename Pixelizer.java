import java.io.File;
import java.io.IOException;
import java.nio.Buffer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Pixelizer {
    BufferedImage ogImage;

    public void Pixelizer(){

    }

    public void imageReader(String path){
        try {
            File inputFile = new File(path);
            BufferedImage image = ImageIO.read(inputFile);
            this.ogImage=image;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    public void imageToPNG(){
        try{
            File outputFile = new File("output.png");
            ImageIO.write(this.ogImage,"PNG",outputFile);

        }catch(IOException e){
            System.out.println("error saving to file");
        }
    }

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
        for(int y=0;y<this.ogImage.getHeight();y+=1){
            for(int x=0;x<this.ogImage.getWidth();x+=1){ 
                int pixelValue = this.ogImage.getRGB(x, y);
                int a=(pixelValue >> 24) & 0xFF;
                int r=(pixelValue >> 16) & 0xFF;
                int g=(pixelValue >> 8) & 0xFF;
                int b=pixelValue & 0xFF;
                int averageRGB=(r+g+b)/3;
                if(averageRGB>threshold*255){
                    a=255;
                    r=255;
                    g=255;
                    b=255;
                }else{
                    a=255;
                    r=0;
                    g=0;
                    b=0; 
                }
                
                int rgbValue = (a << 24) | (r << 16) | (g << 8) | b;
                this.ogImage.setRGB(x, y, rgbValue);
            }
        }
    }
    public static void main(String[] args){
        Pixelizer pixelizer=new Pixelizer();
        pixelizer.imageReader("testImage.jpg");
        //BufferedImage image =pixelizer.reduceResolution(Integer.parseInt(args[0]));
        //pixelizer.toGreyscale();
        pixelizer.toBlackAndWhite(Double.parseDouble(args[1]));
        pixelizer.imageToPNG();
    }
}
