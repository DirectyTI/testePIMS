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

class ServicoPimsTagLab {

    public void TagRequisicao ( ) {

        certificates certifCates = new certificates ( );
        String login = "mla\\gmenegue";
        String senha = "Mosaic@2022";
        String AuthAutorization = login + ":" + senha;
        String TAGCorreta = null;

        try {

            DateFormat formatador = new SimpleDateFormat ( "yyyy-MM-dd" );
            Date data = new Date ( );
            String dataAtual = formatador.format ( data );
            String startTime = "startTime=" + dataAtual + "%2000:00&";
            String endTime = "endTime=" + "2021-02-19" + "%2012:00:00-00&";

            String startTimeBD = dataAtual + " # 00:00";
            String endTimeBD = dataAtual + " # 23:00";

            certifCates.certificateS ( );
            URL url = new URL ( "https://pivision.mosaicco.com/piwebapi/dataservers/F1DSJNMH3B1lQke4iGxscSM3VgQlJDTVRTUlYxMQ/points?selectedFields=Items.WebId;Items.Name;Items.Path;Items.Descriptor;Items.PointType&maxCount=10000" );
            HttpURLConnection connection = (HttpURLConnection) url.openConnection ( );

            String AuthCode = Base64.getEncoder ( ).encodeToString ( ( AuthAutorization ).getBytes ( "UTF-8" ) );
            String authHeaderValue = "Basic " + new String ( AuthCode );
            connection.setRequestProperty ( "Authorization", authHeaderValue );

            connection.setRequestMethod ( "GET" );
            int responseCode = connection.getResponseCode ( );

            if ( responseCode == 200 ) {

                System.out.println ( "Answer of request: " + responseCode + "OK" + " successful" );

            } else {

                System.out.println ( "Answer of request: " + responseCode + "Not OK" + " fail" );

            }

            InputStream content = (InputStream) connection.getInputStream ( );
            BufferedReader in = new BufferedReader ( new InputStreamReader ( content ) );

            String line;
            line = in.readLine ( );

            //----------------------------------------------------tratandoJSON------------------------------

            JSONObject jsonObject = new JSONObject ( line );
            JSONArray arrItens = jsonObject.getJSONArray ( "Items" );

            String[] NameTag = new String[ arrItens.length ( ) ];
            String[] WEBID = new String[ arrItens.length ( ) ];


            int tamanho = arrItens.length ( );


            LeitorArq leArquivo = new LeitorArq ( );
            String[] linhaTagsTxT = leArquivo.leitor ( );

            String enderecoRequiscao = "";

            System.out.println ( "Wait till finish the process. Please don't turn off :)" );

            for (int h = 0; h < leArquivo.contLinha; h++) {

                for (int i = 0; i < arrItens.length ( ); i++) {


                    JSONObject TagItem = arrItens.getJSONObject ( i );

                    NameTag[ i ] = TagItem.getString ( "Name" );
                    WEBID[ i ] = TagItem.getString ( "WebId" );


                    if ( linhaTagsTxT[ h ].equals ( NameTag[ i ] ) ) {


                        String intervalo;
                        System.out.println ( "Tag found: " + linhaTagsTxT[ h ] );

                        /*

                        if(linhaTagsTxT[h].equals("CT-PROD_BIHORARIA-OPC")){

                        intervalo = "summaryType=Maximum&summaryDuration=1h";
                        enderecoRequiscao = "https://pivision.mosaicco.com/piwebapi/streams/" + WEBID[i] + "/" + "summary?selectedFields=Items.Value&" + startTime + "calculationBasis=eventWeighted&" + intervalo;//"interval=1h";

                        }else{

                        intervalo = "summaryType=Average&summaryDuration=1h";
                        enderecoRequiscao = "https://pivision.mosaicco.com/piwebapi/streams/" + WEBID[i] + "/" + "summary?selectedFields=Items.Value&" + startTime + "calculationBasis=eventWeighted&" + intervalo;//"interval=1h";

                        }


                         */


                        if ( linhaTagsTxT[ h ].equals ( "CT-PROD_BIHORARIA-OPC" ) ) {

                            intervalo = "summaryType=Maximum&summaryDuration=1h";

                            enderecoRequiscao = "https://pivision.mosaicco.com/piwebapi/streams/" + WEBID[ i ] + "/" + "summary?selectedFields=Items.Value&" + startTime + "calculationBasis=eventWeighted&" + intervalo;//"interval=1h";
                            URL urlStream = new URL ( enderecoRequiscao );
                            HttpURLConnection connectionStream = (HttpURLConnection) urlStream.openConnection ( );

                            connectionStream.setRequestProperty ( "Authorization", authHeaderValue );
                            connectionStream.setRequestMethod ( "GET" );

                            InputStream contentStream = (InputStream) connectionStream.getInputStream ( );
                            BufferedReader inStream = new BufferedReader ( new InputStreamReader ( contentStream ) );

                            String lineStream;
                            lineStream = inStream.readLine ( );

                            JSONObject jsonObjectStream = new JSONObject ( lineStream );
                            JSONObject Valor = new JSONObject ( );

                            String TIMESTAMP = "";
                            double value = 0;

                            JSONArray teste = jsonObjectStream.getJSONArray ( "Items" );

                            for (int cont = 0; cont < teste.length ( ); cont++) {

                                JSONObject TagItemStream = teste.getJSONObject ( cont );
                                Valor = TagItemStream.getJSONObject ( "Value" );

                                TIMESTAMP = String.valueOf ( Valor.getString ( "Timestamp" ) );
                                value = Float.valueOf ( Valor.getFloat ( "Value" ) );


                                String string = TIMESTAMP;

                                String valorQuePrecisa = string.substring ( 0, 19 ) + "Z";


                                String defaultTimezone = TimeZone.getDefault ( ).getID ( );
                                Date date = ( new SimpleDateFormat ( "yyyy-MM-dd'T'HH:mm:ssZ", new Locale ( "pt", "BR" ) ) ).parse ( valorQuePrecisa.replaceAll ( "Z$", "+0000" ) );
                                String ValorConvertidoTimeStamp = ( new SimpleDateFormat ( "yyyy-MM-dd'T'HH:mm:ssZ", new Locale ( "pt", "BR" ) ) ).format ( date );


                                BDconnection conn = new BDconnection ( );
                                Connection conect = conn.getConnection ( );

                                String sql = "INSERT INTO pims_inf(valor, dat_hor_in, dat_hor_fim, tag, Timestamp) VALUES(? ,? ,? ,? ,?)";
                                PreparedStatement stm = conect.prepareStatement ( sql );

                                stm.setFloat ( 1, (float) value );
                                stm.setString ( 2, startTimeBD );
                                stm.setString ( 3, endTimeBD );
                                stm.setString ( 4, linhaTagsTxT[ h ] );
                                stm.setString ( 5, ValorConvertidoTimeStamp );

                                stm.execute ( );
                                conn.closeDataBaseConnection ( );


                            }


                        } else {

                            intervalo = "summaryType=Average&summaryDuration=1h";

                            enderecoRequiscao = "https://pivision.mosaicco.com/piwebapi/streams/" + WEBID[ i ] + "/" + "summary?selectedFields=Items.Value&" + startTime + "calculationBasis=eventWeighted&" + intervalo;//"interval=1h";
                            URL urlStream = new URL ( enderecoRequiscao );
                            HttpURLConnection connectionStream = (HttpURLConnection) urlStream.openConnection ( );

                            connectionStream.setRequestProperty ( "Authorization", authHeaderValue );
                            connectionStream.setRequestMethod ( "GET" );

                            InputStream contentStream = (InputStream) connectionStream.getInputStream ( );
                            BufferedReader inStream = new BufferedReader ( new InputStreamReader ( contentStream ) );

                            String lineStream;
                            lineStream = inStream.readLine ( );

                            JSONObject jsonObjectStream = new JSONObject ( lineStream );
                            JSONObject Valor = new JSONObject ( );

                            String TIMESTAMP = "";
                            double value = 0;

                            JSONArray teste = jsonObjectStream.getJSONArray ( "Items" );

                            for (int cont = 0; cont < teste.length ( ); cont++) {

                                JSONObject TagItemStream = teste.getJSONObject ( cont );

                                Valor = TagItemStream.getJSONObject ( "Value" );
                                TIMESTAMP = String.valueOf ( Valor.getString ( "Timestamp" ) );


                                if ( Valor.isNull ( "Value" ) ) {

                                    value = 0.0;

                                } else {

                                    value = Float.valueOf ( Valor.getFloat ( "Value" ) );

                                }

                                String string = TIMESTAMP;
                                String ValorConvertidoTimeStamp = "";

                                if ( linhaTagsTxT[ h ].equals ( "CT-USINA_ANALISE_GRANULADO_ANG_GR_TOT_P2O5-RDB" ) ||
                                        linhaTagsTxT[ h ].equals ( "CT-USINA_ANALISE_GRANULADO_CGR_P2O5-RDB" ) ||
                                        linhaTagsTxT[ h ].equals ( "CT-USINA_ANALISE_GRANULADO_CGR_Fe2O3-RDB" )
                                ) {

                                    Date date = ( new SimpleDateFormat ( "yyyy-MM-dd'T'HH:mm:ssZ", new Locale ( "pt", "BR" ) ) ).parse ( string.replaceAll ( "Z$", "+0300" ) );
                                    ValorConvertidoTimeStamp = ( new SimpleDateFormat ( "yyyy-MM-dd'T'HH:mm:ssZ", new Locale ( "pt", "BR" ) ) ).format ( date );

                                } else if ( linhaTagsTxT[ h ].equals ( "CT-USINA_ANALISE_ULTRAFINOS_ANUF_P2O5-RDB" ) ) {

                                    Date date = ( new SimpleDateFormat ( "yyyy-MM-dd'T'HH:mm:ssZ", new Locale ( "pt", "BR" ) ) ).parse ( string.replaceAll ( "Z$", "-0200" ) );
                                    ValorConvertidoTimeStamp = ( new SimpleDateFormat ( "yyyy-MM-dd'T'HH:mm:ssZ", new Locale ( "pt", "BR" ) ) ).format ( date );

                                } else if ( linhaTagsTxT[ h ].equals ( "CT-USINA_ANALISE_ULTRAFINOS_RSUF_P2O5-RDB" ) ) {

                                    Date date = ( new SimpleDateFormat ( "yyyy-MM-dd'T'HH:mm:ssZ", new Locale ( "pt", "BR" ) ) ).parse ( string.replaceAll ( "Z$", "+0200" ) );
                                    ValorConvertidoTimeStamp = ( new SimpleDateFormat ( "yyyy-MM-dd'T'HH:mm:ssZ", new Locale ( "pt", "BR" ) ) ).format ( date );

                                } else if ( linhaTagsTxT[ h ].equals ( "CT-USINA_ANALISE_GRANULADO_RGR_P2O5-RDB" ) ) {

                                    Date date = ( new SimpleDateFormat ( "yyyy-MM-dd'T'HH:mm:ssZ", new Locale ( "pt", "BR" ) ) ).parse ( string.replaceAll ( "Z$", "+0200" ) );
                                    ValorConvertidoTimeStamp = ( new SimpleDateFormat ( "yyyy-MM-dd'T'HH:mm:ssZ", new Locale ( "pt", "BR" ) ) ).format ( date );

                                } else if ( linhaTagsTxT[ h ].equals ( "CT-USINA_GRANOMETRIA_ANG GR TOT_MALHA_65-RDB" ) ) {

                                    Date date = ( new SimpleDateFormat ( "yyyy-MM-dd'T'HH:mm:ssZ", new Locale ( "pt", "BR" ) ) ).parse ( string.replaceAll ( "Z$", "-0100" ) );
                                    ValorConvertidoTimeStamp = ( new SimpleDateFormat ( "yyyy-MM-dd'T'HH:mm:ssZ", new Locale ( "pt", "BR" ) ) ).format ( date );

                                } else {

                                    Date date = ( new SimpleDateFormat ( "yyyy-MM-dd'T'HH:mm:ssZ", new Locale ( "pt", "BR" ) ) ).parse ( string.replaceAll ( "Z$", "+0100" ) );
                                    ValorConvertidoTimeStamp = ( new SimpleDateFormat ( "yyyy-MM-dd'T'HH:mm:ssZ", new Locale ( "pt", "BR" ) ) ).format ( date );

                                }

                                String defaultTimezone = TimeZone.getDefault ( ).getID ( );


                                BDconnection conn = new BDconnection ( );
                                Connection conect = conn.getConnection ( );

                                String sql = "INSERT INTO pims_inf(valor, dat_hor_in, dat_hor_fim, tag, Timestamp) VALUES(? ,? ,? ,? ,?)";
                                PreparedStatement stm = conect.prepareStatement ( sql );

                                stm.setFloat ( 1, (float) value );
                                stm.setString ( 2, startTimeBD );
                                stm.setString ( 3, endTimeBD );
                                stm.setString ( 4, linhaTagsTxT[ h ] );
                                stm.setString ( 5, ValorConvertidoTimeStamp );

                                stm.execute ( );
                                conn.closeDataBaseConnection ( );
                            }
                        }


                    }
                }
            }


        } catch (Exception e) {
            e.getStackTrace ( );
        }

    }

}
