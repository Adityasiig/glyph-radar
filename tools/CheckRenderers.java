public class Chk {
    static String[] N={"BARS","BLOOM","SCAN","HEART","SPIN","RAIN"};
    // The heart is deliberately still between beats, so repeated frames
    // are the design rather than a stutter. Everything else should move
    // on most frames at the service's ~11fps. A single repeated frame
    // between whole pixel steps is unavoidable and invisible; what
    // matters is that no design stalls for several frames together,
    // which is what the run length check below catches.
    static boolean[] STILL_OK={false,false,false,true,false,false};

    static Renderer mk(int i,int s){switch(i){
        case 0:return new BarsRenderer(s);case 1:return new BloomRenderer(s);
        case 2:return new ScanRenderer(s);case 3:return new HeartRenderer(s);
        case 4:return new SpinRenderer(s);default:return new RainRenderer(s);}}
    public static void main(String[] a){
        int S=13; boolean bad=false;
        for(int i=0;i<6;i++){
            Renderer r=mk(i,S);
            int blank=0, frozen=0, minBright=99999, maxBright=0, greyish=0;
            int run=0, maxRun=0;
            int[] prev=null;
            // Sample at the rate the service actually draws.
            for(int f=0;f<200;f++){
                float t=f*0.09f;
                int[] px=r.render(t);
                int bright=0, mids=0;
                for(int v:px){
                    if(v>60) bright++;
                    // Count pixels stuck between levels: at this size a
                    // half lit pixel reads as mud, not as shading.
                    if(v>10 && v<=60) mids++;
                }
                minBright=Math.min(minBright,bright); maxBright=Math.max(maxBright,bright);
                if(bright==0) blank++;
                if(mids>bright*2 && bright<12) greyish++;
                if(prev!=null){
                    boolean same=true;
                    for(int k=0;k<px.length;k++) if(px[k]!=prev[k]){same=false;break;}
                    if(same){ frozen++; run++; maxRun=Math.max(maxRun,run); } else run=0;
                }
                prev=px.clone();
            }
            // A good 13x13 design is never blank, keeps moving, and keeps
            // a solid core of bright pixels rather than a grey haze.
            boolean ok = blank==0 && minBright>=6 && greyish<20
                    && (STILL_OK[i] || maxRun<=2);
            if(!ok) bad=true;
            System.out.printf("%-7s bright=%d..%d blank=%d stall=%d(max %d) muddy=%d  %s%n",
                N[i],minBright,maxBright,blank,frozen,maxRun,greyish, ok?"ok":"<<< CHECK");
        }
        System.out.println(bad?"\nFAIL":"\nALL PASS");
    }
}
