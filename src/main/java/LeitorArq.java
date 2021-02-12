import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
/*
Essa classe Lê o arquivo com as tags e salva em uma Array de strings. É usada na classe requisicaoServidorTag
para verificar quais tags da documentação DIrecty existem no servidor.
 */

public class LeitorArq {
    public int contLinha = 0;
    public String[] leitor() throws IOException {
        String path = "C:\\Tags\\TAGLAB.txt"; // caminho da pasta onde está as tags da documentação
        BufferedReader buffRead = new BufferedReader(new FileReader(path)); // Leitura do arquivo

        String linha = "";

        while (true) {
           if(linha != null){
               contLinha++;
           }else {
               break;
           }
            linha = buffRead.readLine();
        }
        contLinha = contLinha-1; // esse contador está com -1 pois no final da contagem aparece 141 linhas. Na verdade são 140
        buffRead.close();
        System.out.println("\nNumber of tags that will be search: "+contLinha+"\n");

        String[] Linhas = new String[contLinha];
        BufferedReader buffRead2 = new BufferedReader(new FileReader(path));

        for(int i=0;i<contLinha ;i++){

            Linhas[i] = buffRead2.readLine();

        }

        return Linhas; //retorna todas as linhas do arquivo.
    }

}