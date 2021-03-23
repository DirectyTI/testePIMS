/*
* Classe principal.
* Esta classe é reponsável por pela execução completa do sistema de requisição.
* */

import java.io.IOException;

//Classe principal
public class Get_run {
    //Método principal Get_run
    public static void main(String[] args) throws IOException {

        new Thread(Laboratorial).start(); //Thread de execução Laboratorial
        new Thread(Supervisorio).start(); //Thread de execução Supervisório

    }


    /*
    * Programação paralela.
    * Implementação das Threads de Laboratorial e Supervisório
    * */

    //Thread de Laboratorial
    private static Runnable Laboratorial = new Runnable() {
        @Override
        public void run() {
            requisicaoServidorTagLab tag = new requisicaoServidorTagLab();
            tag.TagRequisicao();
        }
    };

    //Thread de Supervisório
    private static Runnable Supervisorio = new Runnable() {
        @Override
        public void run() {
            requisicaoServidorTagSup tag2 = new requisicaoServidorTagSup();
            tag2.reqSupervisorio();
        }
    };

}

