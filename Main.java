//Trabalho Comunicacao entre processos - Sistemas Operacionais
//Gabriel Braz e Santos - 260569

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Main
{

    public static void main(String[] args)
    {
        Scanner scan = new Scanner(System.in);
        String hostaddr = "localhost";
        int port_to_calculadora = 8000;
        int port_from_calculadora = 8001;
        boolean verified = false;
        String text;

        //Obter dados de conexao
        System.out.println("Insira o endereco da calculadora: (Ex: localhost ou 192.168.0.1)");
        hostaddr = scan.next();

        do
        {
            System.out.println("Insira a porta de envio de dados sentido Acionador -> Calculadora: (Ex: 8000)");
            port_to_calculadora = scan.nextInt();

            System.out.println("Insira a porta de envio de dados sentido Calculadora -> Acionador: (Ex: 8001)");
            port_from_calculadora = scan.nextInt();

            if (port_from_calculadora == port_to_calculadora)
            {
                System.out.println("As portas devem ser diferentes!");
            }
        } while (port_from_calculadora == port_to_calculadora);

        //Abre socket para enviar dados para a calculadora
        try (Socket socket = new Socket(hostaddr, port_to_calculadora))
        {
            System.out.println("Calculadora conectada em: " + hostaddr + ":" + port_to_calculadora);

            //Abre steam de input e output de dados dentro do socket
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            //Envio primeiro numero
            do
            {
                System.out.println("------\nInsira o primeiro numero: ");
                text = scan.next();


                try
                {
                    int num = Integer.parseInt(text);
                    verified = true;
                } catch (NumberFormatException e)
                {
                    System.out.println("Nao e um numero valido!");
                    verified = false;
                    continue;
                }

                writer.println(text);
                System.out.println("Enviado!");
                String response = reader.readLine();
                System.out.println("Resposta server (" + response + ")");
            } while (!verified);

            //Envio do segundo numero
            do
            {
                System.out.println("------\nInsira o segundo numero: ");
                text = scan.next();

                try
                {
                    int num = Integer.parseInt(text);
                    verified = true;
                } catch (NumberFormatException e)
                {
                    verified = false;
                    System.out.println("Nao e um numero valido!");
                    continue;
                }

                writer.println(text);
                System.out.println("Enviado!");
                String response = reader.readLine();
                System.out.println("Resposta server (" + response + ")");
            } while (!verified);

            //Envio da operacao a ser realizada
            do
            {
                System.out.println("------\nInsira a operacao desejada: ");
                System.out.println(" + Soma;");
                System.out.println(" - Subtracao;");
                System.out.println(" * Multiplicacao.");
                System.out.println(" / Divisao.");
                text = scan.next();

                if (text.equals("+") || text.equals("-") || text.equals("*") || text.equals("/"))
                {
                    verified = true;
                } else
                {
                    verified = false;
                    System.out.println("Operacao invalida! Insira somente um dos valores a seguir: +, -, /, *");
                    continue;
                }

                writer.println(text);
                System.out.println("Enviado!");
                String response = reader.readLine();
                System.out.println("Resposta server (" + response + ")");
            } while (!verified);

            String response = reader.readLine();
            System.out.println("------\nResposta server (" + response + ")");

            socket.close();

        } catch (UnknownHostException ex)
        {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex)
        {
            System.out.println("I/O error: " + ex.getMessage());
        }

        //Abre socket para receber o resultado da calculadora
        try (ServerSocket serverSocket = new ServerSocket(port_from_calculadora)) {
            System.out.println("------\nObtendo os resultados da calculadora...");
            System.out.println("Esperando conexao na porta: " + port_from_calculadora);

            Socket socket = serverSocket.accept();
            String addr_from_calculadora = socket.getInetAddress().toString().split("/")[1];

            System.out.println("Calculadora conectada! Endereco: " + addr_from_calculadora + ":" + port_from_calculadora);

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            String resultado = reader.readLine();
            writer.println("Resultado obtido!");
            System.out.println("------\nResultado obtido!\n" + resultado);

            writer.println("Okay! Encerrando conexao");
            System.out.println("Conexao encerrada.");

            socket.close();


        } catch (IOException ex) {
            System.out.println("Erro - " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}