public class C2 {
  static String[] N={"CLOCK","BREATHE","ORBIT","WAVE","CORNERS","DRIFT"};
  // Clock tracks real time and Breathe/Corners hold at their extremes,
  // so repeated frames are the design, not a stutter.
  static boolean[] STILL_OK={true,true,false,false,true,false};
  // Orbit lays down a deliberately faint path and Drift is a sparse
  // starfield, so a low lit count and a high dim count are the point
  // of those two rather than a sign of mud.
  static boolean[] SPARSE_OK={false,false,true,false,false,true};
  static Renderer mk(int i,int s){switch(i){
    case 0:return new ClockRenderer(s);case 1:return new BreatheRenderer(s);
    case 2:return new OrbitRenderer(s);case 3:return new WaveRenderer(s);
    case 4:return new CornersRenderer(s);default:return new DriftRenderer(s);}}
  public static void main(String[] a){
    int S=a.length>0?Integer.parseInt(a[0]):13;
    boolean bad=false;
    for(int i=0;i<6;i++){
      Renderer r=mk(i,S);
      int blank=0,minB=99999,maxB=0,muddy=0,run=0,maxRun=0; int[] prev=null;
      for(int f=0;f<220;f++){
        int[] px=r.render(f*0.09f);
        int bright=0,mids=0;
        for(int v:px){ if(v>60)bright++; if(v>10&&v<=60)mids++; }
        minB=Math.min(minB,bright); maxB=Math.max(maxB,bright);
        if(bright==0) blank++;
        // "Classy" here means a lit core, not a grey haze.
        if(mids>bright*3 && bright<6) muddy++;
        if(prev!=null){ boolean same=true;
          for(int k=0;k<px.length;k++) if(px[k]!=prev[k]){same=false;break;}
          if(same){run++;maxRun=Math.max(maxRun,run);} else run=0; }
        prev=px.clone();
      }
      boolean ok = blank==0 && (STILL_OK[i]||maxRun<=2)
        && (SPARSE_OK[i] ? minB>=1 : (minB>=3 && muddy<25));
      if(!ok) bad=true;
      System.out.printf("%-8s bright=%d..%d blank=%d stallmax=%d muddy=%d  %s%n",
        N[i],minB,maxB,blank,maxRun,muddy, ok?"ok":"<<< CHECK");
    }
    System.out.println(bad?"FAIL":"ALL PASS");
  }
}
