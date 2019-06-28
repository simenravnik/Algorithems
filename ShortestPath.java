
import java.io.*;

public class ShortestPath {

    public static void main(String[] args) {

         try {

            //odpre datoteko
            FileReader fileReader = new FileReader(args[0]);
            BufferedReader vhod = new BufferedReader(fileReader);

            //prebere prvo vrstico
            String vrstica = vhod.readLine();
            int maxSopotnikov = Integer.parseInt(vrstica);

            //prebere zacetne koordinate
            vrstica = vhod.readLine();
            String[] vektor = vrstica.split(",");
            int[] zacPozicija = new int[2];
            zacPozicija[0] = Integer.parseInt(vektor[0]);
            zacPozicija[1] = Integer.parseInt(vektor[1]);

            vrstica = vhod.readLine();
            int stPotnikov = Integer.parseInt(vrstica);

            int[][] pozicije = new int[stPotnikov][4];

            //v dvodemenzionalno tabelo shrani vse koordinate potnikov
            for(int i = 0; i < stPotnikov; i++) {
               vrstica = vhod.readLine();
               vektor = vrstica.split(",");
               int potnik = Integer.parseInt(vektor[0])-1;
               for(int j = 1; j < 5; j++)
                  pozicije[potnik][j-1] = Integer.parseInt(vektor[j]);
            }

            int[] str = new int[stPotnikov];
            int[] count = new int[stPotnikov];
            int[] rezultat = new int[stPotnikov*2];
            for(int i =  0; i < stPotnikov; i++) {
               str[i] = i+1;
               count[i] = 2;
            }
            int[] najZaporedje = new int[stPotnikov*2];

            /** gre v rekurzijo */
            izracunaj(str, count, rezultat, najZaporedje, 0, maxSopotnikov, pozicije, zacPozicija, 0);

            //System.out.print(najZaporedje[0]);
            String zaporedje = "" + najZaporedje[0];
            for(int i = 1; i < najZaporedje.length; i++) {
              //System.out.print("," + najZaporedje[i]);
              zaporedje = zaporedje + ("," + najZaporedje[i]);
           }
            //System.out.println();

            BufferedWriter izhod = new BufferedWriter(new FileWriter(args[1]));
            izhod.write(zaporedje);

            izhod.close();
            vhod.close();
         }
         catch(FileNotFoundException ex) {
             System.out.println("Unable to open file '" + args[0] + "'");
         }
         catch(IOException ex) {
             System.out.println("Error reading file '"+ args[0]+ "'");
         }
    }

    public static int minPot = -1;

    public static void izracunaj(int str[], int count[], int rezultat[], int[] najZaporedje, int level, int maxSopotnikov, int[][] pozicije, int[] zacPozicija, int cena) {
        if (level == rezultat.length) {
            int pot =  cena;
            if((minPot == -1 || pot < minPot) && pot != 0) {
              for(int i = 0; i < rezultat.length; i++)
                najZaporedje[i] = rezultat[i];
                minPot = pot;
            }
            return;
        }

        /** zacasna pozicija za premik */
        int[] tmpPozicija = new int[2];
        tmpPozicija[0] = zacPozicija[0];
        tmpPozicija[1] = zacPozicija[1];

        /** rekurzija preveri vse mozne kombinacije (variacije) stevil od 1 do n, pri cemer se vsako stevilo ponovi dvakrat */
        for(int i = 0; i < str.length; i++) {
            if(count[i] == 0 || maxSopotnikov<0) {
                continue;
            }
            rezultat[level] = str[i];
            count[i]--;
            int trenutnaCena;
            int skupno;
            if(count[i] == 1)
            {
                /** Ko se premaknemo kar poracunamo ceno */
                trenutnaCena = (Math.abs(tmpPozicija[0] - pozicije[rezultat[level]-1][0]) + Math.abs(tmpPozicija[1] - pozicije[rezultat[level]-1][1]));
                skupno = cena+trenutnaCena;
                if(skupno > minPot && minPot != -1) {
                   count[i]++;
                   continue;
                }
                tmpPozicija[0] = pozicije[rezultat[level]-1][0];
                tmpPozicija[1] = pozicije[rezultat[level]-1][1];
                izracunaj(str, count, rezultat, najZaporedje, level+1, maxSopotnikov-1, pozicije, tmpPozicija, cena + trenutnaCena);
            }
            else
            {
                /** Enako tukaj */
                trenutnaCena = (Math.abs(tmpPozicija[0] - pozicije[rezultat[level]-1][2]) + Math.abs(tmpPozicija[1] - pozicije[rezultat[level]-1][3]));
                skupno = cena+trenutnaCena;
                if(skupno > minPot && minPot != -1) {
                   count[i]++;
                   continue;
                }
                tmpPozicija[0] = pozicije[rezultat[level]-1][2];
                tmpPozicija[1] = pozicije[rezultat[level]-1][3];
                izracunaj(str, count, rezultat, najZaporedje, level+1, maxSopotnikov+1, pozicije, tmpPozicija, cena + trenutnaCena);
            }

            /** Preden gremo testirati drugo opcijo resetiramo pozicijo */
            tmpPozicija[0] = zacPozicija[0];
            tmpPozicija[1] = zacPozicija[1];
            count[i]++;
        }
    }

}
