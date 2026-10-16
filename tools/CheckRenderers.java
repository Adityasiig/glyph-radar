public class Chk {
    static String[] N={"RADAR","EQUALISER","ORBIT","WAVEFORM","PULSE","RAIN"};
    // Radar's dial ring and Rain's falling columns are meant to reach
    // the rim; everything else touching it means a clipped shape.
    static boolean[] RIM_OK={true,false,false,false,false,true};
    static Renderer mk(int i,int s){switch(i){
        case 0:return new RadarRenderer(s);case 1:return new EqualiserRenderer(s);
        case 2:return new OrbitRenderer(s);case 3:return new WaveformRenderer(s);
        case 4:return new PulseRenderer(s);default:return new RainRenderer(s);}}
    public static void main(String[] a){
        int S=13; float c=(S-1)/2f; boolean bad=false;
        for(int i=0;i<6;i++){
            // Sweep 8s of animation; track whether any lit pixel ever lands
            // on the clip boundary ring, which means a shape was cut off.
            int edgeHits=0, blankFrames=0, staticFrames=0;
            int[] prev=null; int minLit=99999,maxLit=0;
            for(int f=0;f<160;f++){
                float t=f*0.05f;
                int[] px=mk(i,S).render(t);
                int lit=0;
                for(int y=0;y<S;y++)for(int x=0;x<S;x++){
                    int v=px[y*S+x]; if(v>40)lit++;
                    double d=Math.hypot(x-c,y-c);
                    // A bright pixel in the outermost half-pixel band
                    // means the design is pressed against the clip.
                    if(v>150 && d>c-0.25) edgeHits++;
                }
                minLit=Math.min(minLit,lit); maxLit=Math.max(maxLit,lit);
                if(lit==0) blankFrames++;
                if(prev!=null){boolean same=true;
                    for(int k=0;k<px.length;k++) if(Math.abs(px[k]-prev[k])>6){same=false;break;}
                    if(same) staticFrames++;}
                prev=px.clone();
            }
            boolean ok = blankFrames==0 && staticFrames<8 && minLit>4
                    && (RIM_OK[i] || edgeHits<40);
            if(!ok) bad=true;
            System.out.printf("%-10s lit=%d..%d blank=%d static=%d edge=%d  %s%n",
                N[i],minLit,maxLit,blankFrames,staticFrames,edgeHits, ok?"ok":"<<< CHECK");
        }
        System.out.println(bad?"\nFAIL":"\nALL PASS");
    }
}
