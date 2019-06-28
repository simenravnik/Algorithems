import java.io.*;

public class Defragmentation {

	/** definirane public tabele, da so vidne vsem metodam */
	public static int[][] ponastavitev;
	public static int[][] najPostavitevIndeksov;
	public static int[] najPostavitev;

	public static void main(String[] args) {
		try {
			//odpre datoteko
         BufferedReader vhod = new BufferedReader(new FileReader(args[0]));

			String line;
			String celota = "";

			//prebere celotno vhodno datoteko v en string
			while((line = vhod.readLine()) != null) {
				celota+=(line+",");
			}

			String[] vektor = celota.split(",");

			//shranjene dolzine blokov
			int[] dolzine = new int[vektor.length/3+1];

			//shranjeni zacetki in konci blokov
			int[][] indeksi = new int[vektor.length/3+1][2];
			int[] rezultat = new int[vektor.length/3];

			int[] count = new int[vektor.length/3+1];
	 		  for(int i = 0; i<count.length; i++) {
	 			 count[i] = 1;
	 		}

			int[] zacetnaPostavitev = new int[vektor.length/3];

			//tabela za ponastavljanje postavitve kasneje
			int[] postavitev = new int[vektor.length/3];

			// tabela za posnastavljanje indeksov kasneje
			ponastavitev = new int[vektor.length/3+1][2];

			//forloop kjer se nastavijo vse vrednosti
			int idx = 0;
			for(int i = 0; i < vektor.length; i+=3) {
					postavitev[idx] = Integer.parseInt(vektor[i]);
					zacetnaPostavitev[idx] = postavitev[idx];
					idx++;

					int zac = Integer.parseInt(vektor[i+1]);
					int kon = Integer.parseInt(vektor[i+2]);

					indeksi[Integer.parseInt(vektor[i])][0] = zac;
					indeksi[Integer.parseInt(vektor[i])][1] = kon;

					ponastavitev[Integer.parseInt(vektor[i])][0] = zac;
					ponastavitev[Integer.parseInt(vektor[i])][1] = kon;

					dolzine[Integer.parseInt(vektor[i])] = kon-zac+1;
			}

			najPostavitevIndeksov = new int[vektor.length/3+1][2];
			najPostavitev = new int[postavitev.length];

			/** gremo v rekurzijo, kjer pregledamo vse moznosti in zraven izlocamo neustrezne */
			izracunaj(zacetnaPostavitev, indeksi, count, 0, rezultat, dolzine, postavitev);

			BufferedWriter izhod = new BufferedWriter(new FileWriter(args[1]));

			String zaporedje ="";
			for(int i = 0; i < najPostavitev.length; i++) {
				if(najPostavitevIndeksov[najPostavitev[i]][0] != ponastavitev[najPostavitev[i]][0]) {
					zaporedje = najPostavitev[i] + "," + najPostavitevIndeksov[najPostavitev[i]][0];
					izhod.write(zaporedje);
					izhod.newLine();
					//System.out.println(najPostavitev[i] + "," + najPostavitevIndeksov[najPostavitev[i]][0]);
				}
			}

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

	public static int najCena = -1;

	public static void izracunaj(int str[], int[][] indeksi, int count[], int level, int rezultat[], int[] dolzine, int[] postavitev) {
		 if (level == rezultat.length) {
			 int cena = 0;
			 //System.out.println("Konec");
			 for(int i = 0; i< rezultat.length; i++)
				 if(indeksi[rezultat[i]][0] != ponastavitev[rezultat[i]][0])
					 cena+= dolzine[rezultat[i]];

			if(cena < najCena || najCena == -1) {
				 najCena = cena;
				 for(int i = 0; i < rezultat.length; i++) {
					najPostavitev[i] = rezultat[i];
					//System.out.print(najPostavitev[i]);
					najPostavitevIndeksov[rezultat[i]][0] = indeksi[rezultat[i]][0];
					najPostavitevIndeksov[rezultat[i]][1] = indeksi[rezultat[i]][1];
				 }
			 }
			  return;
		 }

		 /** rekurzija preveri vse mozne kombinacije (variacije) stevil od 1 do n, pri cemer se vsako stevilo ponovi dvakrat */
		 for(int i = 0; i < str.length; i++) {
			  if(count[i] == 0) continue;

			  rezultat[level] = str[i];
			  count[i]--;

				if(!preveri(postavitev, indeksi, rezultat, dolzine, level+1)) {
					count[i]++;
					continue;
				}

			  izracunaj(str, indeksi, count, level+1, rezultat, dolzine, postavitev);
			  count[i]++;
			  ponastavi(indeksi, ponastavitev, postavitev, str);
		 }
	}

	/** funkcija preveri ali je trenutna razporeditev do level ustrena ali ne */
	public static boolean preveri(int[] postavitev, int[][] indeksi, int[] rezultat, int[] dolzine, int level) {
		int cena = 0;
		int idx = 0;
		int zacNaslednje = 0;
		for(int i = 0; i< rezultat.length; i++) {
			if(rezultat[i] == 0) return true;
			if(rezultat[i] == postavitev[idx]) {
				int zacetek = indeksi[postavitev[idx]][0];
				indeksi[postavitev[idx]][0] = zacNaslednje;
				indeksi[postavitev[idx]][1] = indeksi[postavitev[idx]][0]+dolzine[postavitev[idx]]-1;
				zacNaslednje += dolzine[postavitev[idx]];
				if(indeksi[postavitev[idx]][0] != zacetek) cena += dolzine[postavitev[idx]];
				idx++;
				if(cena > najCena && najCena != -1) return false;
				if(idx == rezultat.length || idx == level) return true;
			} else if(rezultat[i] != postavitev[idx]) {
				if(dolzine[rezultat[i]] > (indeksi[postavitev[idx]][0] - zacNaslednje)) {
					return false;
				} else {
					prestaviId(postavitev, rezultat[i], idx);
					indeksi[rezultat[i]][0] = zacNaslednje;
					indeksi[rezultat[i]][1] = indeksi[rezultat[i]][0] + dolzine[rezultat[i]]-1;
					zacNaslednje += dolzine[postavitev[idx]];
					cena += dolzine[postavitev[idx]];
					if(cena > najCena && najCena != -1) return false;
					idx++;
					if(idx == rezultat.length || idx == level) return true;
				}
			}
		}
		return true;
	}

	/** ponastavi indekse nazaj v prvotno stanje */
	public static void ponastavi(int[][] indeksi, int[][] ponastavitev, int[] postavitev, int[] zacetnaPostavitev) {
		for(int i = 1; i < indeksi.length; i++) {
			indeksi[i][0] = ponastavitev[i][0];
			indeksi[i][1] = ponastavitev[i][1];
		}
		for(int i = 0; i < zacetnaPostavitev.length; i++)
			postavitev[i] = zacetnaPostavitev[i];
	}

	/** prestavi id na ustrezno mesto */
	public static void prestaviId(int[] tabela, int id, int idx) {
		int a = idx;
		while(a < tabela.length) {
			if(tabela[a] == id) break;
			a++;
		}

		for(int j = a; j > idx; j--)
			tabela[j] = tabela[j-1];
		tabela[idx] = id;
   }
}
