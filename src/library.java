
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class library {
	private static final String FILE_NAME = "books.txt";
	

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		File file = new File(FILE_NAME);

		// created a file
		try {
			if (!file.exists()) {
				file.createNewFile();
				System.out.println(FILE_NAME + " file created.");
			}
		} catch (IOException e) {
			System.out.println("File could not be created: " + e.getMessage());
			return;
		}

		boolean running = true;
		while (running) {
			System.out.println("\n--- Library Menu ---");
			System.out.println("1) Add Book");
			System.out.println("2) Show Books");
			System.out.println("3) Search Book");
			System.out.println("4) Delete Book (by index)");
			System.out.println("5) Delete Book (by exact name)");
			System.out.println("6) Exit");
			System.out.print("Your choice: ");
			String choice = sc.nextLine().trim();

			switch (choice) {
			case "1":
				addBook(sc, file);
				break;
			case "2":
				listBooks(file);
				break;
			case "3":
				searchBooks(sc, file);
				break;
			case "4":
				deleteByIndex(sc, file);
				break;
			case "5":
				deleteByName(sc, file);
				break;
			case "6":
				running = false;
				System.out.println("Exiting program...");
				break;
			default:
				System.out.println("Invalid option. Try again.");
			}
		}

		sc.close();
	}

	// 1.add book
	private static void addBook(Scanner sc, File file) {
		System.out.print("Book name: ");
		String bookName = sc.nextLine().trim();
		if (bookName.isEmpty()) {
			System.out.println("Book name cannot be empty.");
			return;
		}
		System.out.print("Author name: ");
		String author = sc.nextLine().trim();
		if (author.isEmpty()) {
			System.out.println("Author name cannot be empty.");
			return;
		}

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
			bw.write(bookName + " - " + author);
			bw.newLine();
			System.out.println("Book saved.");
		} catch (IOException e) {
			System.out.println("Write error: " + e.getMessage());
		}
	}

	// 2.list books
	private static void listBooks(File file) {
		List<String> lines = readAllLines(file);
		if (lines.isEmpty()) {
			System.out.println("File is empty.");
			return;
		}
		System.out.println("\n--- Books List ---");
		for (int i = 0; i < lines.size(); i++) {
			System.out.println((i + 1) + ") " + lines.get(i));
		}
	}

	// 3.search books (partial match, case-insensitiv)
	private static void searchBooks(Scanner sc, File file) {
		System.out.print("Enter search term (book name or author): ");
		String term = sc.nextLine().trim().toLowerCase();
		if (term.isEmpty()) {
			System.out.println("Search term cannot be empty.");
			return;
		}

		List<String> lines = readAllLines(file);
		boolean found = false;
		for (int i = 0; i < lines.size(); i++) {
			String lower = lines.get(i).toLowerCase();
			if (lower.contains(term)) {
				if (!found) {
					System.out.println("\nMatching results:");
				}
				System.out.println((i + 1) + ") " + lines.get(i));
				found = true;
			}
		}
		if (!found) {
			System.out.println("No matching record found.");
		}
	}

	// 4.delete by index
	private static void deleteByIndex(Scanner sc, File file) {
		List<String> lines = readAllLines(file);
		if (lines.isEmpty()) {
			System.out.println("File is empty. Nothing to delete.");
			return;
		}
		listBooks(file);
		System.out.print("Enter the number of the record to delete: ");
		String input = sc.nextLine().trim();
		int idx;
		try {
			idx = Integer.parseInt(input);
		} catch (NumberFormatException e) {
			System.out.println("Enter a valid number.");
			return;
		}
		if (idx < 1 || idx > lines.size()) {
			System.out.println("Invalid index.");
			return;
		}

		if (rewriteExcludingIndex(file, idx - 1)) {
			System.out.println("Record deleted: " + lines.get(idx - 1));
		} else {
			System.out.println("Deletion failed.");
		}
	}

	// 5. Delete by exact name
	private static void deleteByName(Scanner sc, File file) {
		System.out.print("Enter exact book and author name (e.g., Book Name - Author): ");
		String target = sc.nextLine().trim();
		if (target.isEmpty()) {
			System.out.println("Input cannot be empty.");
			return;
		}

		List<String> lines = readAllLines(file);
		boolean removed = false;
		List<String> kept = new ArrayList<>();
		for (String line : lines) {
			if (!removed && line.equals(target)) {
				removed = true;
			} else {
				kept.add(line);
			}
		}

		if (!removed) {
			System.out.println("No exact matching record found.");
			return;
		}

		if (writeAllLines(file, kept)) {
			System.out.println("Record deleted: " + target);
		} else {
			System.out.println("Error during deletion.");
		}
	}

	// Help: read all lines from file
	private static List<String> readAllLines(File file) {
		List<String> lines = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.trim().isEmpty())
					lines.add(line);
			}
		} catch (IOException e) {
			System.out.println("Read error: " + e.getMessage());
		}
		return lines;
	}

	// Helper: write all lines to file (overwrite)
	private static boolean writeAllLines(File file, List<String> lines) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
			for (String l : lines) {
				bw.write(l);
				bw.newLine();
			}
			return true;
		} catch (IOException e) {
			System.out.println("Write error: " + e.getMessage());
			return false;
		}
	}

	// Helper: rewrite file excluding specific index
	private static boolean rewriteExcludingIndex(File file, int excludeIndex) {
		List<String> lines = readAllLines(file);
		if (excludeIndex < 0 || excludeIndex >= lines.size())
			return false;
		List<String> kept = new ArrayList<>();
		for (int i = 0; i < lines.size(); i++) {
			if (i == excludeIndex)
				continue;
			kept.add(lines.get(i));
		}
		return writeAllLines(file, kept);
	}
}
