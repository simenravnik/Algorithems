import java.io.*;

public class Cache {

	public static void main(String[] args) {

		try {
			//odpre datoteko
         BufferedReader vhod = new BufferedReader(new FileReader(args[0]));

         //prebere prvo vrstico
         String vrstica = vhod.readLine();
         int stUkazov = Integer.parseInt(vrstica);

			/** odpremo pomnilnik */
			Pomnilnik pomnilnik = new Pomnilnik();

         for(int i = 0; i < stUkazov; i++) {
         	vrstica = vhod.readLine();
         	String[] vektor = vrstica.split(",");
         	String ukaz = vektor[0];
         	switch(ukaz) {
         		case "i" :
         			pomnilnik.init(Integer.parseInt(vektor[1]));
         			break;
         		case "a" :
         			pomnilnik.alloc(Integer.parseInt(vektor[1]), Integer.parseInt(vektor[2]));
         			break;
         		case "f" :
         			pomnilnik.free(Integer.parseInt(vektor[1]));
         			break;
         		case "d" :
         			pomnilnik.defrag(Integer.parseInt(vektor[1]));
         			break;
         	}
         }
			pomnilnik.izpisi(args[1]);
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

class LinkedListElement {
	int id;
	int zacetniIndex;
	int koncniIndex;
	int size;
	LinkedListElement next;

	LinkedListElement(int id, int zacIndex, int konIndex, int size) {
		this.id = id;
		this.zacetniIndex = zacIndex;
		this.koncniIndex = konIndex;
		this.size = size;
		this.next = null;
	}
}

class Pomnilnik {
	protected LinkedListElement first;
	protected LinkedListElement last;

	private int[] polje;

	Pomnilnik() {
		makenull();
	}

	//Funkcija makenull inicializira seznam
	public void makenull() {
		//header
		first = new LinkedListElement(-1, -1, -1, -1);
		last = null;
	}

	//Funkcija addLast doda nov element na konec seznama
	public void addLast(int id, int zacIndex, int konIndex, int size)
	{
		//najprej naredimo nov element
		LinkedListElement newEl = new LinkedListElement(id, zacIndex, konIndex, size);
		first.next = newEl;
		newEl.next = null;
	}

	//Funkcija addLast doda nov element na naslednje mesto v seznamu
	public void addNext(LinkedListElement el, int id, int zacIndex, int konIndex, int size) {
		//najprej naredimo nov element
		LinkedListElement newEl = new LinkedListElement(id, zacIndex, konIndex, size);
		newEl.next = el.next;
		el.next = newEl;
	}

	/** funkcija inicializira staticno polje velikosti size */
	public void init(int size) {
		this.polje = new int[size];
	}

	/** Fuknicja alocira blok velikosti size in id-jem id in ga vstavi na ustrezno mesto */
	public boolean alloc(int size, int id) {
		LinkedListElement el = first;

		/** ce je seznam prazen ga ustavimo na zadnje mesto */
		if(el.next == null && size < polje.length) {
			addLast(id, 0, size-1, size);
			return true;
		}
		if(size < polje.length) {
			while(el != null) {
				/** element vstavimo za elementom el */
				if(el.next != null) {
					if((el.next.zacetniIndex - el.koncniIndex-1) >= size) {
						addNext(el, id, el.koncniIndex + 1, el.koncniIndex + size, size);
						return true;
					}
				} else if(el.next == null) {
					/** smo na koncu seznama in element vstavimo na zadnje mesto */
					if((polje.length - el.koncniIndex -1) >= size) {
						addNext(el, id, el.koncniIndex + 1, el.koncniIndex + size, size);
						return true;
					}
				}
				el = el.next;
			}
		}
		return false;
	}

	/** Fuknija brise blok z id-jem id */
	public int free(int id) {
		LinkedListElement el = first;

		/** najprej poiscemo clen z id-jem id, in ga nato zbrisemo */
		while(el != null) {
			if(el.next != null && el.next.id == id) {
				int velikost = el.next.size;
				el.next = el.next.next;
				return velikost;
			}
			el = el.next;
		}
		return 0;
	}

	/** Funkcija defragmentira vrzeli znotraj polja */
	public void defrag(int n) {
		LinkedListElement el = first;

		/** najprej poiscemo prvi blok ki ni defragmentiran */
		while(el != null) {
			if(el.next != null) {
				if(el.koncniIndex+1 != el.next.zacetniIndex) {
					break;
				}
			}
			el = el.next;
		}
		/** nato n-krat defragmentiramo */
		while(n > 0 && el != null) {
			if(el.next != null) {
				el.next.zacetniIndex = el.koncniIndex+1;
				el.next.koncniIndex = el.next.zacetniIndex+el.next.size-1;
			}
			el = el.next;
			n--;
		}
	}

	public void izpisi(String args1) {
		try {
			/** odpremo izhodno datoteko in vanjo pisemo */
			BufferedWriter izhod = new BufferedWriter(new FileWriter(args1));
			LinkedListElement el = first.next;
			String zaporedje = "";
			while(el != null) {
				zaporedje =el.id+","+el.zacetniIndex+","+el.koncniIndex;
				izhod.write(zaporedje);
				izhod.newLine();
				//System.out.println(el.id + "," + el.zacetniIndex + "," + el.koncniIndex);
				el = el.next;
			}
		  izhod.close();
		}
		catch (FileNotFoundException ex)
		{
		  System.out.println("Unable to open file '" + args1 + "'");
		}
		catch (IOException ex)
		{
		  System.out.println("Error reading file '" + args1 + "'");
		}
	}
}
