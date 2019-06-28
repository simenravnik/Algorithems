
import java.io.*;

public class Dynamic {

	public static void main(String[] args) {

		try {

			//odpre datoteko
				FileReader fileReader = new FileReader(args[0]);
				BufferedReader vhod = new BufferedReader(fileReader);

				//prebere prvo vrstico
				String vrstica = vhod.readLine();
				int stUkazov = Integer.parseInt(vrstica);

				LinkedList list = new LinkedList();

				for(int i = 0; i < stUkazov; i++) {
					vrstica = vhod.readLine();
					String[] vektor = vrstica.split(",");
					String ukaz = vektor[0];
					switch(ukaz) {
						case "i" :
							list.init(Integer.parseInt(vektor[1]), Integer.parseInt(vektor[2]));
							break;
						case "a" :
							list.alloc(Integer.parseInt(vektor[1]), Integer.parseInt(vektor[2]));
							break;
						case "f" :
							list.free(Integer.parseInt(vektor[1]));
							break;
					}
				}

				BufferedWriter izhod = new BufferedWriter(new FileWriter(args[1]));

				int[] stBajtov = list.izpisi();

				String zaporedje="";

				for(int i = stBajtov.length-1; i > -1; i--) {
					//System.out.println(stBajtov[i]);
					zaporedje =""+ stBajtov[i];
					izhod.write(zaporedje);
					izhod.newLine();
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
}
	/** list element */
	class LinkedListElement {
		public int indeks;
		public int[] tabela;
		public LinkedListElement next;

		LinkedListElement(int idx, int n) {
			indeks = idx;
			tabela = new int[n];
			next = null;
		}
	}

	/** seznam */
	class LinkedList {
		protected int stNezasedenih[];

		protected LinkedListElement first;
		protected LinkedListElement last;

		LinkedList() {
			makenull();
		}

		//Funkcija makenull inicializira seznam
		public void makenull() {
			first = new LinkedListElement(-1, 0);
			last = null;
		}

		public void init(int m, int n) {
			stNezasedenih = new int[m];
			for(int i = 0; i < m; i++) {
				addLast(i, n);
			}
			for(int i = 0; i < stNezasedenih.length; i++) {
				stNezasedenih[i] = n;
			}
		}

		//Funkcija addLast doda nov element na konec seznama
		public void addLast(int idx, int velikostTabele) {
			LinkedListElement newEl = new LinkedListElement(idx, velikostTabele);
			//ce je prazen seznam
			if (last == null) {
				//ce seznam vsebuje samo en element, kazalca "first" in "last" kazeta na glavo seznama
				first.next = newEl;
				last = first;
			} else {
				last.next.next = newEl;
				last = last.next;
			}
		}


		/** funkcija ustrezno poisce mesto na katero bo vstavilo blok dolzine size in ga nato vstavi */
		public boolean alloc(int size, int id) {
			int ustrezen = -1;
			int stProstih = -1;
			for(int i = 0; i < stNezasedenih.length; i++) {
				if(stNezasedenih[i] >= size) {
					if(stProstih == -1) {
						stProstih = stNezasedenih[i];
						ustrezen = i;
					}
					if(stNezasedenih[i] < stProstih) {
						stProstih = stNezasedenih[i];
						ustrezen = i;
					}
				}
			}
			if(ustrezen == -1) return false;

			LinkedListElement el;
			el = first;

			int index = el.next.tabela.length - stNezasedenih[ustrezen];
			stNezasedenih[ustrezen] -= size;

			while(el.indeks != ustrezen) {
				el = el.next;
			}

			int velikost = size;
			for(int i = index; i < el.tabela.length && velikost > 0; i++) {
				if(el.tabela[i] == 0) {
					el.tabela[i] = id;
					velikost--;
				}
			}
			return true;
		}

		/** funkcija poisce mesto kjer se nahaja blok z id-jem id, ga izbrise in ostale bloke zamakne da se izognemo vrzeli */
		public int free(int id) {
			LinkedListElement el;
			el = first;

			boolean jeNajden = false;
			while(el != null) {
				for(int i = 0; i < el.tabela.length; i++) {
					if(el.tabela[i] == id) {
						jeNajden = true;
						break;
					}
				}
				if(jeNajden) break;
				el = el.next;
			}

			if(!jeNajden) return 0;

			int indeksI = -1;
			int IndeksIzacetni = -1;

			for(int i = 0; i < el.tabela.length; i++) {
				if(el.tabela[i] == id && IndeksIzacetni == -1) {
					IndeksIzacetni = i;
				}
				if(el.tabela[i] == id) {
					el.tabela[i] = 0;
					indeksI = i;
					//jePocisceno = true;
					stNezasedenih[el.indeks]++;
				}
			}

			int stevec = 0;
			int tmp = IndeksIzacetni;
			for(int i = indeksI+1; i < el.tabela.length; i++) {
				el.tabela[tmp] = el.tabela[i];
				tmp++;
				stevec++;
			}

			while(tmp != el.tabela.length) {
				el.tabela[tmp] = 0;
				tmp++;
			}

			return 1;
		}

		/** vse skupaj se vrnemo v obliki tabele in izpisemo */
		public int[] izpisi() {

			LinkedListElement el;
			el = first;

			int[] stBajtov = new int[last.tabela.length+1];
			for(int i = 0; i < stNezasedenih.length; i++) {
				stBajtov[stNezasedenih[i]]++;
			}
			return stBajtov;
		}
	}
