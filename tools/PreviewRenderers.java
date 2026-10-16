public class Sim {
    static String[] NAMES = {"RADAR","EQUALISER","ORBIT","WAVEFORM","PULSE","RAIN"};
    static Renderer mk(int i,int s){
        switch(i){
            case 0: return new RadarRenderer(s);
            case 1: return new EqualiserRenderer(s);
            case 2: return new OrbitRenderer(s);
            case 3: return new WaveformRenderer(s);
            case 4: return new PulseRenderer(s);
            default: return new RainRenderer(s);
        }
    }
    public static void main(String[] a){
        int S=13;
        float[] ts={0.4f,1.9f,3.3f};
        for(int i=0;i<6;i++){
            Renderer r=mk(i,S);
            System.out.println("\n##### "+NAMES[i]+" #####");
            for(float t:ts){
                int[] px=r.render(t);
                int lit=0; long sum=0; int mx=0;
                StringBuilder[] rows=new StringBuilder[S];
                for(int y=0;y<S;y++){rows[y]=new StringBuilder();
                    for(int x=0;x<S;x++){int v=px[y*S+x];
                        sum+=v; if(v>40)lit++; if(v>mx)mx=v;
                        rows[y].append(v>200?"##":v>130?"++":v>70?"::":v>25?"..":"  ");
                    }}
                System.out.println("t="+t+"  lit="+lit+" max="+mx+" mean="+(sum/(S*S)));
                for(StringBuilder sb:rows) System.out.println(sb);
            }
        }
    }
}
