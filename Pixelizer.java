import java.io.File;
import java.io.IOException;
import java.nio.Buffer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Pixelizer {
    BufferedImage ogImage;

    public void Pixelizer(){

    }

    public BufferedImage imageReader(String path){
        try {
            File inputFile = new File(path);
            BufferedImage image = ImageIO.read(inputFile);
            this.ogImage=image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this.ogImage;
    }

    public BufferedImage reduceResolution(int stepSize){
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
        return newImage;
    }

    public void imageToPNG(BufferedImage image){
        try{
            File outputFile = new File("output.png");
            ImageIO.write(image,"PNG",outputFile);

        }catch(IOException e){
            System.out.println("error saving to file");
        }
    }

    public BufferedImage toBlackAndWhite(BufferedImage image){
        return image;
    }
    
    public static void main(String[] args){
        Pixelizer pixelizer=new Pixelizer();
        pixelizer.imageReader("testImage.jpg");
        BufferedImage image =pixelizer.reduceResolution(Integer.parseInt(args[0]));
        pixelizer.imageToPNG(image);
    }
}
