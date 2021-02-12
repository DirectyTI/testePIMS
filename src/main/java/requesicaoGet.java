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

public class requesicaoGet {

    public void realizarRequisicao() {

        certificates certifCates = new certificates();
        String login = "mla\\gmenegue";
        String senha = "Mosaic@2021";
        String AuthAutorization = login + ":" + senha;

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date dataHoraAtual = new Date();
        System.out.println(dataHoraAtual);
        System.out.println(dateFormat.format(dataHoraAtual));
        String DataAtual = dateFormat.format(dataHoraAtual);

        //https://pivision.mosaicco.com/piwebapi/dataservers/
        //https://pivision.mosaicco.com/piwebapi/streams/F1DPJNMH3B1lQke4iGxscSM3VgowAAAAQlJDTVRTUlYxMVxDVC1BSk1fMTUwNjAzLU9QQw/interpolated?startTime=2020-05-05 00:00&endTime=2020-05-05 12:00&interval=1h
        //https://pivision.mosaicco.com/piwebapi/dataservers/F1DSJNMH3B1lQke4iGxscSM3VgQlJDTVRTUlYxMQ/points?nameFilter=*150603*&selectedFields=Items.WebId;Items.Name;Items.Path;Items.Descriptor;Items.PointType&maxCount=100

        String WebID = "F1DPJNMH3B1lQke4iGxscSM3VgowAAAAQlJDTVRTUlYxMVxDVC1BSk1fMTUwNjAzLU9QQw";

        try {
            certifCates.certificateS();
            URL url = new URL("https://pivision.mosaicco.com/piwebapi/streams/" + WebID + "/interpolated?startTime=2020-11-05%2000:00&endTime=2020-11-05%2012:00&interval=1h");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            String AuthCode = Base64.getEncoder().encodeToString((AuthAutorization).getBytes("UTF-8"));
            String authHeaderValue = "Basic " + new String(AuthCode);
            connection.setRequestProperty("Authorization", authHeaderValue);

            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            System.out.println(responseCode);

            InputStream content = (InputStream) connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(content));

            String line;
            line = in.readLine();

            System.out.println(line);

            //----------------------------------------------------tratandoJSON------------------------------

            JSONObject jsonObject = new JSONObject(line);
            //JSONObject itens = jsonObject.getJSONObject("Timestamp");
            JSONArray arrItens = jsonObject.getJSONArray("Items");
            
            for (int i = 0; i < arrItens.length(); i++) {
                /*=========================Conexão com o banco de dados =========================================*/
                BDconnection conn = new BDconnection();
                Connection conect = conn.getConnection();
                /*=========================Conexão com o banco de dados =========================================*/
             //   requisicaoServidorTag reqtag = new requisicaoServidorTag();

                JSONObject f = arrItens.getJSONObject(i);
                System.out.println("Timestamp:" + f.getString("Timestamp"));
                System.out.println("");

                System.out.println("Value:" + f.getFloat("Value"));

             //   String TAG =  reqtag.TagRequisicao();

                String sql = "INSERT INTO pims_inf(valor, dat_hor_in, dat_hor_fim, tag) VALUES(? ,? ,? ,?)";
                PreparedStatement stm = conect.prepareStatement(sql);

                stm.setFloat(1, f.getFloat("Value"));
                stm.setString(2,DataAtual);
                stm.setString(3,DataAtual);
              //  stm.setString(4,TAG);

                stm.execute();
                conn.closeDataBaseConnection();
            }
        } catch (Exception e) {
            e.getStackTrace();
        }

    }

}
