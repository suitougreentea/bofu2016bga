package io.github.suitougreentea.bofu2016bga;

import processing.core.PApplet;
import processing.core.PImage;

public class Convolver{
    int radius;
    int kernelSize;
    int[] kernel;
    int[][] mult;

    Convolver(int sz){
        this.setRadius(sz);
    }

    void setRadius(int sz){

        int i,j;
        sz=PApplet.min(PApplet.max(1,sz),248);
        if (radius==sz) return;
        kernelSize=1+sz*2;
        radius=sz;
        kernel= new int[1+sz*2];
        mult=new int[1+sz*2][256];

        int sum=0;
        for (i=1;i<sz;i++){
            int szi=sz-i;
            kernel[sz+i]=kernel[szi]=szi*szi;
            sum+=kernel[szi]+kernel[szi];
            for (j=0;j<256;j++){
                mult[sz+i][j]=mult[szi][j]=kernel[szi]*j;
            }
        }
        kernel[sz]=sz*sz;
        sum+=kernel[sz];
        for (j=0;j<256;j++){
            mult[sz][j]=kernel[sz]*j;
        }
    }

    void blur(PImage img, int x, int y, int w, int h){

        int sum,cr,cg,cb,k;
        int pixel,read,i,ri,xl,yl,yi,ym,riw;
        int[] pix=img.pixels;
        int iw=img.width;

        int wh=iw*img.height;

        int r[]=new int[wh];
        int g[]=new int[wh];
        int b[]=new int[wh];

        for (i=0;i<wh;i++){
            ri=pix[i];
            r[i]=(ri&0xff0000)>>16;
            g[i]=(ri&0x00ff00)>>8;
            b[i]=(ri&0x0000ff);
        }

        int r2[]=new int[wh];
        int g2[]=new int[wh];
        int b2[]=new int[wh];

        x= PApplet.max(0,x);
        y=PApplet.max(0,y);
        w=x+w-PApplet.max(0,(x+w)-iw);
        h=y+h-PApplet.max(0,(y+h)-img.height);
        yi=y*iw;

        for (yl=y;yl<h;yl++){
            for (xl=x;xl<w;xl++){
                cb=cg=cr=sum=0;
                ri=xl-radius;
                for (i=0;i<kernelSize;i++){
                    read=ri+i;
                    if (read>=x && read<w){
                        read+=yi;
                        cr+=mult[i][r[read]];
                        cg+=mult[i][g[read]];
                        cb+=mult[i][b[read]];
                        sum+=kernel[i];
                    }
                }
                ri=yi+xl;
                r2[ri]=cr/sum;
                g2[ri]=cg/sum;
                b2[ri]=cb/sum;
            }
            yi+=iw;
        }
        yi=y*iw;

        for (yl=y;yl<h;yl++){
            ym=yl-radius;
            riw=ym*iw;
            for (xl=x;xl<w;xl++){
                cb=cg=cr=sum=0;
                ri=ym;
                read=xl+riw;
                for (i=0;i<kernelSize;i++){
                    if (ri<h && ri>=y){
                        cr+=mult[i][r2[read]];
                        cg+=mult[i][g2[read]];
                        cb+=mult[i][b2[read]];
                        sum+=kernel[i];
                    }
                    ri++;
                    read+=iw;
                }
                pix[xl+yi]=0xff000000 | (cr/sum)<<16 | (cg/sum)<<8 | (cb/sum);
            }
            yi+=iw;
        }
    }
}

