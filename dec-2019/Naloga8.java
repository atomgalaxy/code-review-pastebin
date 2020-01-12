import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Math;

public class Naloga8{
	
	public static void main(String[] args) {
		
		String ime_vhoda = args[0];
		String ime_izhoda = args[1];
		
		String pot_vhod = "C:\\Users\\krneki322\\Documents\\Programiranje\\aps1\\aps-sem2-nal8\\Naloga8_testniPrimeri\\" + ime_vhoda;
		String pot_izhod = "C:\\Users\\krneki322\\Documents\\Programiranje\\aps1\\aps-sem2-nal8\\\\Naloga8_testniPrimeri\\" + ime_izhoda;
		
		File vhod = new File(pot_vhod);
		File izhod = new File(pot_izhod);
		
		Problem problem = preberiPodatke(vhod);
		
		int stNodov = stNodov(problem.dimenzija);
		
		PrintWriter p = null;
		try {
			p = new PrintWriter(new FileWriter(pot_izhod));
		} catch (IOException e1) {

			e1.printStackTrace();
		}
		
		for(int i = 0; i< problem.stUkazov; i++) {
			Node[] drevo = pretvori(problem, stNodov);
			int[] test = poplavi(drevo, problem.ukazi[i], problem.dimenzija);
			StringBuffer vrstica = print(test);
			p.println(vrstica);
		}
		p.close();
		
	}
	public static Problem preberiPodatke(File vhod) {
		
		Problem problem = new Problem();
		Scanner scan = null;
		try {
			scan = new Scanner(vhod).useDelimiter("\\D");
			
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		
		int dimenzija = scan.nextInt();
		scan.nextLine();
		
		problem.dimenzija = dimenzija;
		
		//zmeljevid visin
		int[][] zemljevid = new int[dimenzija][dimenzija];
		
		for(int i = 0; i < dimenzija; i++) {
			
			String[] vrstica = scan.nextLine().split(",");
			
			for(int j = 0; j < dimenzija; j++) {
				zemljevid[i][j] = Integer.parseInt(vrstica[j]);
			}
		}
		problem.zemljevid = zemljevid;
		
		int stUkazov = scan.nextInt();
		scan.nextLine();
		problem.stUkazov = stUkazov;
		
		int[] ukazi = new int[stUkazov];
		
		for(int i = 0; i < stUkazov; i++) {
			ukazi[i] = scan.nextInt();
			scan.nextLine();
		}
		problem.ukazi = ukazi;
		
		scan.close();
		
		return problem;
	}
	//dobi inta, ki sta rezultat enega ukaza in zapise v file
	public static StringBuffer print(int[] vrstica) {
		
		int stPop = vrstica[0];
		int stPrebranih = vrstica[1];
		StringBuffer rezultat = new StringBuffer();
		
		rezultat.append(stPop);
		rezultat.append(",");
		rezultat.append(stPrebranih);
		
		return rezultat;
	}
	//vzame zemljevid visin in vrne tabelo drevesa
	public static Node[] pretvori(Problem p, int stNodov) {
		Node[] drevo = new Node[stNodov];
		int counter = stNodov-1;
	
		for(int i = counter; i >= 0; i--) {
			drevo[i] = calculateMinMax(i, p, drevo);
		}
		return drevo;
	}
	public static int stNodov(int dimenzija) {
		int stN = dimenzija*dimenzija;
		int rez = stN;
		while(stN > 1) {
			stN = stN/4;
			rez +=stN;
		}
		return rez;
	}
	//vrne rezultat po poplavljanju
	public static int[] poplavi(Node[] tree, int visM, int dimenzija) {
		Node[] drevo = tree;
		int[] rez = new int[2];
		int field = dimenzija*dimenzija;
		int covCounter = 0; //steje kok mest pokrivajo presteti nodi
		int floodCounter = 0;
		int visitCounter = 0;
		
		for(int i = 0; i < drevo.length; i++) {
			if(covCounter == field) {
				rez[0] = floodCounter;
				rez[1] = visitCounter;
				return rez;
			}
			if(drevo[i] == null) {
				continue;
			}
			if(drevo[i].min == drevo[i].max) {
				deleteBelow(drevo, i);
			}
			//morje ne poplavi nicesar
			if(visM < drevo[i].min ){
				visitCounter++;
				covCounter += getCoverage(i, dimenzija);
				deleteBelow(drevo, i);
				//continue;
			}
			//morje poplavi vse kar pokriva node
			else if(visM >= drevo[i].max) {
				int tempCov = getCoverage(i, dimenzija);
				visitCounter++;
				floodCounter += tempCov;
				covCounter += tempCov;
			
				deleteBelow(drevo, i);
			}
			else if(visM >= drevo[i].min && visM < drevo[i].max){
				visitCounter++;
			}
		}
		if(covCounter == field) {
			rez[0] = floodCounter;
			rez[1] = visitCounter;
		}
		return rez;
		
	}
	public static int getCoverage(int indeks, int dimenzija) {
		
		int field = dimenzija*dimenzija;
		int nivo = getLevel(indeks);
		
		int coverage = field;
		
		for(int i = 0; i < nivo; i++) {
			coverage /= 4;
		}
		
		return coverage;
	}
	//prejme indeks in zbriše vse veje iz njega
	//dodej da èe je span tega kar se briše 1 sam to zbriše pa ne isce sinov
	public static void deleteBelow(Node[] tree, int indeks) {
		
		Node[] drevo = tree;
		int deleteCounter = 4;
		int level = getLevel(indeks);
		int dolzinaVrstice = (int) Math.pow(4, level);
		int son = nthSonIndeks(indeks, 0);
		int tmp = son;
		
		for(int i = son; i < drevo.length; i++) {
			
			
			for(int j = tmp; j < tmp+deleteCounter; j++) {
				if(j == drevo.length) {
					break;
				}
				drevo[j] = null;
			}
			son = nthSonIndeks(son, 0);
			level++;
			dolzinaVrstice *= 4;
			deleteCounter *= 4;
			i = son;
			tmp = i;
		}
		
	}
	//vzame indeks oèeta vrne indeks ntega sina 
	public static int nthSonIndeks(int indeks, int stevSina) {
		
		return indeks*4+stevSina+1;
	}
	//vrne indeks starša
	public static int parentIndeks(int indeks) {
		
		if(indeks == 0) {
			return -1;
		}
		
		return (indeks-1)/4;
	}
	//vrne nivo na katerem je indeks
	public static int getLevel(int indeks) {

		int level = 0;
		while(indeks != 0) {
			indeks = parentIndeks(indeks);
			level++;
		}
		
		return level;
	}
	//vrne kateri sin je (od 0 do 3)
	public static int sonIdentifier(int indeks) {
		return (indeks-1) % 4;
	}
	public static int getSpan(int indeks, int dimenzija) {
		if(indeks == 0) {
			return dimenzija;
		}
		int parent_span = getSpan(parentIndeks(indeks), dimenzija);
		return parent_span/2;
	}
	//vrne x y v tabeli nadmorskih visin
	public static Koordinata leviZgornjiKot(int indeks, int dimenzija) {
		
		if(indeks == 0) {
			return new Koordinata(0, 0);
		}
		
		int id = sonIdentifier(indeks);
		
		Koordinata parentKot = leviZgornjiKot(parentIndeks(indeks), dimenzija);
		//System.out.print(parentIndeks(indeks) + ": " + parentKot.x + ", " + parentKot.y + "\n");
		int span = getSpan(indeks, dimenzija);
		
		if(id == 1) {
			parentKot.x += span;
		} else if(id == 2) {
			parentKot.y += span;
		} else if(id == 3) {
			parentKot.x += span;
			parentKot.y += span;
		}
		return parentKot;
	}
	//podamo indeks oceta iz drevesa, vrne min max obmocja
	public static Node calculateMinMax(int indeks, Problem p, Node[] tree) {
		Node[] drevo = tree;
		int span = getSpan(indeks, p.dimenzija);
		if(span == 1) {
			Koordinata lzk = leviZgornjiKot(indeks, p.dimenzija);
			int vrednost = p.zemljevid[lzk.y][lzk.x];
			
			return new Node(vrednost, vrednost);
		}
		
		int min = drevo[nthSonIndeks(indeks, 0)].min;
		int max = drevo[nthSonIndeks(indeks, 0)].max;
		for (int son=1; son < 4; ++son) {
			Node sonNode = drevo[nthSonIndeks(indeks, son)];
			int son_min = sonNode.min;
			if (son_min < min) { min = son_min; }
			int son_max = sonNode.max;
			if (son_max > max) { max = son_max; }
		}
		
		return new Node(min, max);
	}
}
class Problem{
	int dimenzija;
	int[][] zemljevid;
	int stUkazov;
	int[] ukazi;
}
class Koordinata{
	int x;
	int y;
	
	public Koordinata(int x, int y) {
		this.x = x;
		this.y = y;
	}
}
class Node{
	int min;
	int max;
	
	public Node(int min, int max) {
		this.min = min;
		this.max = max;
	}
}