/*
* Classe responsável pela requisição das tags do Supervisorio
*
* */

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class requisicaoServidorTagSup {

    public void reqSupervisorio() {

        certificates certifCates = new certificates(); //Instancia dos certificados HTTP
        String login = "mla\\gmenegue"; //Login de autorização da requisição
        String senha = "Mosaic@2023";   //Senha de autorização da requisição
        String AuthAutorization = login + ":" + senha; //String completa de da requisição

        String TAGCorreta = null;

        try {

            //--------------Bloco responsável pela tratatica das datas--------------//

            DateFormat formatador = new SimpleDateFormat("yyyy-MM-dd");
            Date data = new Date();

            String dataAtual = formatador.format(data);
            String startTime = "startTime=" + dataAtual + "%2000:00&";
            String endTime = "endTime=" + "2021-02-18" + "%2012:00:00-00&";
            String startTimeBD = dataAtual + " # 00:00";
            String endTimeBD = dataAtual + " # 23:00";

            //-------------- Fim do bloco responsável pela tratatica das datas--------------//

            certifCates.certificateS();
            //Url de requisição
            URL url = new URL("https://pivisionciu.mosaicco.com/piwebapi/dataservers/F1DSJNMH3B1lQke4iGxscSM3VgQlJDTVRTUlYxMQ/points?selectedFields=Items.WebId;Items.Name;Items.Path;Items.Descriptor;Items.PointType&maxCount=10000");

            //Abrindo a conexão
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //-----------Bloco de autenticação de requisição-----------
            String AuthCode = Base64.getEncoder().encodeToString((AuthAutorization).getBytes("UTF-8"));
            String authHeaderValue = "Basic " + new String(AuthCode);
            connection.setRequestProperty("Authorization", authHeaderValue);
            //-----------Fim do Bloco de autenticação de requisição-----------

            //tipo de requisição
            connection.setRequestMethod("GET");

            //Código da resposta da requisição
            int responseCode = connection.getResponseCode();

            /**
             * Caso retorne 200, a requisição continuará. Caso retorne outro código
             * a requisição será parada.
             */
            if (responseCode == 200) {
                System.out.println("Answer of request: " + responseCode + "OK" + " successful");
            } else {
                System.out.println("Answer of request: " + responseCode + "Not OK" + " fail");
            }

            InputStream content = (InputStream) connection.getInputStream();

            //Leitura dos dados retornados pelo serivodor
            BufferedReader in = new BufferedReader(new InputStreamReader(content));

            String line;
            //Cada linha de leitura sera armazenada na String "line"
            line = in.readLine();

            //----------------------------------------------------tratandoJSON------------------------------//

            //--------------------Bloco de código responsável por tratar a resposta como um JSON----------//
            JSONObject jsonObject = new JSONObject(line);
            JSONArray arrItens = jsonObject.getJSONArray("Items");
            //--------------------Fim do Bloco de código responsável por tratar a resposta como um JSON-----//

            //Armazenamento de dados
            String[] NameTag = new String[arrItens.length()];
            String[] WEBID = new String[arrItens.length()];

            //Armazena o tamanho do array retornado pela requisição
            int tamanho = arrItens.length();

            //Chama o leitor do arquivo Supervisório
            LeitorArqSup leArquivo = new LeitorArqSup();
            String[] linhaTagsTxT = leArquivo.leitor();

            String enderecoRequiscao = "";

            System.out.println("Wait till finish the process. Please don't turn off :)");

            /**
             * O bloco a seguir tem a função de montar uma nova string de conexão
             * passando o webID a data e os parâmetros de requisição conforme a documentação do PIMS
             * indica.
             *
             * O primeiro For indica a indexação da linha do documento contendo as TAGS.
             * O segundo For realiza uma verificação das tags contidas no documento e as tags que existem
             * no servidor PIMS.
             */

            //Lê os arquivos
            for (int h = 0; h < leArquivo.contLinha; h++) {

                //For de verificação
                for (int i = 0; i < arrItens.length(); i++) {

                    JSONObject TagItem = arrItens.getJSONObject(i);

                    NameTag[i] = TagItem.getString("Name");
                    WEBID[i] = TagItem.getString("WebId");


                    if (linhaTagsTxT[h].equals(NameTag[i])) {

                        String intervalo;
                        System.out.println("Tag found: " + linhaTagsTxT[h]);

                        //Tratativa de algumas tags
                        if(linhaTagsTxT[h].equals("CT-PROD_BIHORARIA-OPC")){

                            intervalo = "summaryType=Maximum&summaryDuration=2h";
                            enderecoRequiscao = "https://pivisionciu.mosaicco.com/piwebapi/streams/" + WEBID[i] + "/" + "summary?" + startTime + "calculationBasis=eventWeighted&" + intervalo;//"interval=1h";

                        }else{

                             intervalo = "summaryType=Average&summaryDuration=2h";
                            enderecoRequiscao = "https://pivisionciu.mosaicco.com/piwebapi/streams/" + WEBID[i] + "/" + "summary?" + startTime + "calculationBasis=eventWeighted&" + intervalo;//"interval=1h";

                        }

                        //Nova url para trazer as informações
                        enderecoRequiscao = "https://pivisionciu.mosaicco.com/piwebapi/streams/" + WEBID[i] + "/" + "summary?selectedFields=Items.Value&" + startTime  +  intervalo;//"interval=1h";
                        URL urlStream = new URL(enderecoRequiscao);
                        HttpURLConnection connectionStream = (HttpURLConnection) urlStream.openConnection();

                        connectionStream.setRequestProperty("Authorization", authHeaderValue);
                        connectionStream.setRequestMethod("GET");

                        InputStream contentStream = (InputStream) connectionStream.getInputStream();
                        BufferedReader inStream = new BufferedReader(new InputStreamReader(contentStream));

                        String lineStream;
                        lineStream = inStream.readLine();

                        JSONObject jsonObjectStream = new JSONObject(lineStream);

                        String TIMESTAMP = "";
                        double value = 0;

                        JSONArray teste = jsonObjectStream.getJSONArray("Items");
                        JSONObject Valor = new JSONObject();
                        for(int cont = 0;cont < teste.length(); cont ++){

                            JSONObject TagItemStream = teste.getJSONObject(cont);
                            Valor = TagItemStream.getJSONObject("Value");

                            TIMESTAMP = String.valueOf(Valor.getString("Timestamp"));

                            if(Valor.isNull("Value")){

                                value = 0.0;

                            }else{

                                value = Float.valueOf(Valor.getFloat("Value"));
                            }

                            //Tratativa do fusohorário
                            String string = TIMESTAMP;
                            String valorQuePrecisa = TIMESTAMP.substring(0, 19) + "Z";
                            String ValorConvertidoTimeStamp = "";

                            String defaultTimezone = TimeZone.getDefault().getID();
                            Date date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", new Locale("pt", "BR"))).parse(valorQuePrecisa.replaceAll("Z$", "+0000"));
                             ValorConvertidoTimeStamp = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ",  new Locale("pt", "BR"))).format(date);

                             //Conexão com o banco
                            BDconnection conn = new BDconnection();
                            Connection conect = conn.getConnection();

                            //String SQL
                            String sql = "INSERT INTO pims_inf(valor, dat_hor_in, dat_hor_fim, tag, Timestamp) VALUES(? ,? ,? ,? ,?)";
                            PreparedStatement stm = conect.prepareStatement(sql);

                            stm.setFloat(1, (float) value);
                            stm.setString(2,startTimeBD);
                            stm.setString(3,endTimeBD);
                            stm.setString(4,linhaTagsTxT[h]);
                            stm.setString(5,ValorConvertidoTimeStamp);

                            //Insersão no banco
                            stm.execute();
                            conn.closeDataBaseConnection();
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
