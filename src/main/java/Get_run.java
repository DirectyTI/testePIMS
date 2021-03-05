import java.io.IOException;

public class Get_run {

    public static void main(String[] args) throws IOException {

        new Thread(Laboratorial).start();
        new Thread(Supervisorio).start();


    }


    /*
    * implementando thread
    * */

    private static Runnable Laboratorial = new Runnable() {
        @Override
        public void run() {
            requisicaoServidorTagLab tag = new requisicaoServidorTagLab();
            tag.TagRequisicao();
        }
    };

    private static Runnable Supervisorio = new Runnable() {
        @Override
        public void run() {
            requisicaoServidorTagSup tag2 = new requisicaoServidorTagSup();
            tag2.reqSupervisorio();
        }
    };

}

