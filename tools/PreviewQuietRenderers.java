public class V2 {
  static String[] N={"CLOCK","BREATHE","ORBIT","WAVE","CORNERS","DRIFT"};
  static Renderer mk(int i,int s){switch(i){
    case 0:return new ClockRenderer(s);case 1:return new BreatheRenderer(s);
    case 2:return new OrbitRenderer(s);case 3:return new WaveRenderer(s);
    case 4:return new CornersRenderer(s);default:return new DriftRenderer(s);}}
  public static void main(String[] a){
    int S = a.length>0?Integer.parseInt(a[0]):13;
    float[] ts={0.3f,1.2f,2.6f};
    for(int i=0;i<6;i++){
      System.out.println("\n##### "+N[i]+" #####");
      for(float t:ts){
        int[] px=mk(i,S).render(t);
        int lit=0; for(int v:px) if(v>60) lit++;
        System.out.println("t="+t+"  bright="+lit);
        for(int y=0;y<S;y++){StringBuilder sb=new StringBuilder();
          for(int x=0;x<S;x++){int v=px[y*S+x];
            sb.append(v>200?"##":v>130?"++":v>60?"::":v>10?"..":"  ");}
          System.out.println(sb);}
      }
    }
  }
}
