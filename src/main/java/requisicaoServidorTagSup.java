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

        certificates certifCates = new certificates();
        String login = "mla\\gmenegue";
        String senha = "Mosaic@2022";
        String AuthAutorization = login + ":" + senha;

        String TAGCorreta = null;
        try {
            //gerando a data atual

            DateFormat formatador = new SimpleDateFormat("yyyy-MM-dd");
            Date data = new Date();
            String dataAtual = formatador.format(data);
            //String startTime = "startTime=" + dataAtual + "%2000:00&";
            String startTime = "startTime=" + dataAtual + "%2000:00&";
            String endTime = "endTime=" + "2021-02-18" + "%2012:00:00-00&";

            String startTimeBD = dataAtual + " # 00:00";
            String endTimeBD = dataAtual + " # 23:00";

            certifCates.certificateS();
            URL url = new URL("https://pivision.mosaicco.com/piwebapi/dataservers/F1DSJNMH3B1lQke4iGxscSM3VgQlJDTVRTUlYxMQ/points?selectedFields=Items.WebId;Items.Name;Items.Path;Items.Descriptor;Items.PointType&maxCount=10000");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            String AuthCode = Base64.getEncoder().encodeToString((AuthAutorization).getBytes("UTF-8"));
            String authHeaderValue = "Basic " + new String(AuthCode);
            connection.setRequestProperty("Authorization", authHeaderValue);

            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                System.out.println("Answer of request: " + responseCode + "OK" + " successful");
            } else {
                System.out.println("Answer of request: " + responseCode + "Not OK" + " fail");
            }


            InputStream content = (InputStream) connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(content));

            String line;
            line = in.readLine();

            //  System.out.println(line);

            //----------------------------------------------------tratandoJSON------------------------------

            JSONObject jsonObject = new JSONObject(line);
            //JSONObject itens = jsonObject.getJSONObject("Timestamp");
            JSONArray arrItens = jsonObject.getJSONArray("Items");

            String[] NameTag = new String[arrItens.length()];
            // String[] NameTagVALUE = new String[arrItens.length()];
            String[] WEBID = new String[arrItens.length()];


            int tamanho = arrItens.length();

            //Implementado
            LeitorArqSup leArquivo = new LeitorArqSup();
            String[] linhaTagsTxT = leArquivo.leitor();

            String enderecoRequiscao = "";
            // String intervalo = "summaryType=Average&summaryDuration=2h";
            // System.out.println(intervalo);

            System.out.println("Wait till finish the process. Please don't turn off :)");
            for (int h = 0; h < leArquivo.contLinha; h++) {
                for (int i = 0; i < arrItens.length(); i++) {
                    //NameTag[i]

                    JSONObject TagItem = arrItens.getJSONObject(i);
                    // JSONObject TagItemVALUE = arrItens.getJSONObject(i);


                    //  System.out.println("WebID:" + TagItem.getString("WebId"));
                    // NameTagVALUE[i] = TagItemVALUE.getString("Name");
                    NameTag[i] = TagItem.getString("Name");
                    WEBID[i] = TagItem.getString("WebId");


                    if (linhaTagsTxT[h].equals(NameTag[i])) {


                        String intervalo;
                        System.out.println("Tag found: " + linhaTagsTxT[h]);

                        intervalo = "summaryType=Average&summaryDuration=2h";
                        //linhaTagsTxT[h].equals(NameTag[i])
                        // enderecoRequiscao = "https://pivision.mosaicco.com/piwebapi/streams/" + WEBID[i] + "/" + "interpolated?" + startTime + endTime  + "interval=1h";
                        enderecoRequiscao = "https://pivision.mosaicco.com/piwebapi/streams/" + WEBID[i] + "/" + "summary?selectedFields=Items.Value&" + startTime  +  intervalo;//"interval=1h";
                        URL urlStream = new URL(enderecoRequiscao);
                        HttpURLConnection connectionStream = (HttpURLConnection) urlStream.openConnection();
                        //startTime=2020-12-25%2000:00&endTime=2020-12-25%2012:00  --  startTime + endTime
                        connectionStream.setRequestProperty("Authorization", authHeaderValue);
                        connectionStream.setRequestMethod("GET");

                        InputStream contentStream = (InputStream) connectionStream.getInputStream();
                        BufferedReader inStream = new BufferedReader(new InputStreamReader(contentStream));

                        String lineStream;
                        lineStream = inStream.readLine();

                        JSONObject jsonObjectStream = new JSONObject(lineStream);
                        JSONObject Valor = new JSONObject();

                        String TIMESTAMP = "";
                        double value = 0;

                        JSONArray teste = jsonObjectStream.getJSONArray("Items");

                        for(int cont = 0;cont < teste.length(); cont ++){

                            JSONObject TagItemStream = teste.getJSONObject(cont);
                            Valor = TagItemStream.getJSONObject("Value");



                            TIMESTAMP = String.valueOf(Valor.getString("Timestamp"));
                            //implementando
                            if(Valor.isNull("Value")){

                                value = 0.0;

                            }else{

                                value = Float.valueOf(Valor.getFloat("Value"));
                            }
                            //implementando
                            //value = Float.valueOf(Valor.getFloat("Value"));


                            String string = TIMESTAMP;

                            String defaultTimezone = TimeZone.getDefault().getID();
                            Date date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", new Locale("pt", "BR"))).parse(string.replaceAll("Z$", "+0000"));


                            String ValorConvertidoTimeStamp = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ",  new Locale("pt", "BR"))).format(date);

                            BDconnection conn = new BDconnection();
                            Connection conect = conn.getConnection();

                            String sql = "INSERT INTO pims_inf(valor, dat_hor_in, dat_hor_fim, tag, Timestamp) VALUES(? ,? ,? ,? ,?)";
                            PreparedStatement stm = conect.prepareStatement(sql);

                            stm.setFloat(1, (float) value);
                            stm.setString(2,startTimeBD);
                            stm.setString(3,endTimeBD);
                            stm.setString(4,linhaTagsTxT[h]);
                            stm.setString(5,ValorConvertidoTimeStamp);

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