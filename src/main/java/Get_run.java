import java.io.IOException;

public class Get_run {

    public static void main(String[] args) throws IOException {

        requisicaoServidorTagLab tag = new requisicaoServidorTagLab();
        tag.TagRequisicao();

        requisicaoServidorTagSup tag2 = new requisicaoServidorTagSup();
        tag2.reqSupervisorio();

    }
}

