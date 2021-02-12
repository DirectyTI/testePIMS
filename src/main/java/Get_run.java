import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;

public class Get_run {

    public static void main(String[] args) throws IOException {

        requisicaoServidorTag tag = new requisicaoServidorTag();
        tag.TagRequisicao();

    }
}

