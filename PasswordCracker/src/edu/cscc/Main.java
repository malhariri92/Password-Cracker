package edu.cscc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Generate hashed password / plain password dictionary. Include words as both all lower
 * case and capitalized first letter
 * 
 * @author student name
 *
 */
public class Main {
	private static final String FNAME = "words.txt";
	private final static String PASSWDFNAME = "passwd.txt";
	private static Map<String, String> dictionary = new Hashtable<>();
    private static int slice = -1;
	public static void main(String[] args) {
		// Create dictionary of plain / hashed passwords from list of words
		System.out.println("Generating dictionary ...");
		long start = System.currentTimeMillis();
		createDictionary(FNAME);
		System.out.println("Generated " + dictionary.size() + " hashed passwords in dictionary");
		long stop = System.currentTimeMillis();
		System.out.println("Elapsed time: " + (stop - start) + " milliseconds");
		
		// Read password file, hash plaintext passwords and lookup in dictionary
		System.out.println("\nCracking password file ...");
		start = System.currentTimeMillis();
		crackPasswords(PASSWDFNAME);
		stop = System.currentTimeMillis();
		System.out.println("Elapsed time: " + (stop - start) + " milliseconds");
	}
	
	private static void createDictionary(String fname) {
		// Read in list of words
		List<String> words = new ArrayList<>();
		try (Scanner input = new Scanner(new File(fname));) {
			while (input.hasNext()) {
				String s = input.nextLine();
				if (s != null && s.length() >= 4) {
					words.add(s);
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("File " + FNAME + " not found");
			e.printStackTrace();
			System.exit(-1);
		}
		
		// Generate dictionary from word list
		List<Thread> tlist = new ArrayList<>();
		
		for(int i = 0; i < 4; i++) {
			Thread t = new Thread(new Runnable() {
				public void run() {
					slice++;
					addToDictionary(slice , words);							
				}				
			});
			tlist.add(t);
		}	
        for (Thread t : tlist) t.start();
		
		for (Thread t : tlist) {
			try {
				t.join();
			} catch (InterruptedException e) {
				System.out.println("Got InterruptedException");
			}
		}
	}
	
	private static void crackPasswords(String fname) {
		File pfile = new File(fname);
		try (Scanner input = new Scanner(pfile);) {
			while (input.hasNext()) {
				String s = input.nextLine();
				String[] t = s.split(",");
				String userid = t[0];
				String hashedPassword = t[1];
				String password = dictionary.get(hashedPassword);				
				if (password != null) {
					System.out.println("CRACKED - user: "+userid+" has password: "+password);
				}
			}
		} catch (FileNotFoundException ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
			System.exit(-1);
		}
	}

	private static void generateHashes(String word) {
		// Convert word to lower case, generate hash, store dictionary entry
		String s = word.toLowerCase();
		String sync = new String();
		String hashedStr = HashUtils.hashPassword(s);
		synchronized (sync) {
		dictionary.put(hashedStr, s);
		}
		// Capitalize word, generate hash, store dictionary entry
		s = s.substring(0, 1).toUpperCase() + s.substring(1);
		hashedStr = HashUtils.hashPassword(s);
		synchronized (sync) {
			dictionary.put(hashedStr, s);
		}
	}
	
	public static void addToDictionary(int slice, List<String> list) {
		
		for(int j = slice; j < list.size(); j+= 4) {
			
			generateHashes(list.get(j));			
		}						
	}
}
