package igrek.todotree.services.datatree;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import igrek.todotree.exceptions.NoSuperItemException;
import igrek.todotree.logger.Logs;
import igrek.todotree.services.datatree.serializer.TreeSerializer;
import igrek.todotree.services.filesystem.FilesystemService;
import igrek.todotree.services.filesystem.PathBuilder;
import igrek.todotree.services.preferences.Preferences;

//TODO RESPONSIBILITY separation
public class TreeManager {
	
	private FilesystemService filesystem;
	
	private Preferences preferences;
	
	private TreeSerializer treeSerializer;
	
	private TreeItem rootItem;
	private TreeItem currentItem;
	
	private TreeItem editItem = null;
	private Integer newItemPosition = null;
	
	private List<Integer> selectedPositions = null;
	
	private List<TreeItem> clipboard = null;
	
	private HashMap<TreeItem, Integer> storedScrollPositions;
	
	/** czy wystąpiło pierwsze wejście wgłąb elementu */
	public boolean firstGoInto = false;
	
	public TreeManager(FilesystemService filesystem, Preferences preferences, TreeSerializer treeSerializer) {
		this.filesystem = filesystem;
		this.preferences = preferences;
		this.treeSerializer = treeSerializer;
		reset();
	}
	
	public void reset() {
		rootItem = new TreeItem(null, "/");
		currentItem = rootItem;
		editItem = null;
		storedScrollPositions = new HashMap<>();
	}
	
	public TreeItem getRootItem() {
		return rootItem;
	}
	
	public void setRootItem(TreeItem rootItem) {
		this.rootItem = rootItem;
		this.currentItem = rootItem;
	}
	
	public TreeItem getCurrentItem() {
		return currentItem;
	}
	
	public TreeItem getEditItem() {
		return editItem;
	}
	
	public void setEditItem(TreeItem editItem) {
		this.editItem = editItem;
		this.newItemPosition = null;
	}
	
	public void setNewItemPosition(Integer newItemPosition) {
		this.editItem = null;
		this.newItemPosition = newItemPosition;
	}
	
	public Integer getNewItemPosition() {
		return newItemPosition;
	}
	
	//  NAWIGACJA
	
	public void goUp() throws NoSuperItemException {
		if (currentItem == rootItem) {
			throw new NoSuperItemException();
		} else if (currentItem.getParent() == null) {
			throw new IllegalStateException("parent = null. To się nie powinno zdarzyć");
		} else {
			currentItem = currentItem.getParent();
		}
	}
	
	public void goInto(int childIndex, Integer scrollPos) {
		if (scrollPos != null) {
			storeScrollPosition(currentItem, scrollPos);
		}
		TreeItem item = currentItem.getChild(childIndex);
		goTo(item);
	}
	
	public void goTo(TreeItem child) {
		currentItem = child;
	}
	
	public void goToRoot() {
		goTo(rootItem);
	}
	
	//  DODAWANIE / USUWANIE ELEMENTÓW
	
	public void addToCurrent(TreeItem newItem) {
		currentItem.add(newItem);
	}
	
	public void deleteFromCurrent(int location) {
		currentItem.remove(location);
	}
	
	//  EDYCJA
	
	public void saveItemContent(TreeItem item, String content) {
		item.setContent(content);
	}
	
	public void saveItemContent(String content) {
		if (editItem != null) {
			editItem.setContent(content);
		}
	}
	
	//  ZAPIS / ODCZYT Z PLIKU
	
	public void loadRootTree() {
		
		PathBuilder dbFilePath = filesystem.pathSD().append(preferences.dbFilePath);
		Logs.info("Wczytywanie bazy danych z pliku: " + dbFilePath.toString());
		if (!filesystem.exists(dbFilePath.toString())) {
			Logs.warn("Plik z bazą danych nie istnieje. Domyślna pusta baza danych.");
			return;
		}
		try {
			String fileContent = filesystem.openFileString(dbFilePath.toString());
			TreeItem rootItem = treeSerializer.loadTree(fileContent);
			setRootItem(rootItem);
			Logs.info("Wczytano bazę danych.");
		} catch (IOException | ParseException e) {
			Logs.error(e);
		}
	}
	
	public void saveRootTree() {
		//TODO: wyjście bez zapisywania bazy jeśli nie było zmian
		
		PathBuilder dbFilePath = filesystem.pathSD().append(preferences.dbFilePath);
		//        Logs.info("Zapisywanie bazy danych do pliku: " + dbFilePath.toString());
		try {
			String output = treeSerializer.saveTree(getRootItem());
			filesystem.saveFile(dbFilePath.toString(), output);
		} catch (IOException e) {
			Logs.error(e);
		}
	}
	
	
	//  ZMIANA KOLEJNOŚCI
	
	public void replace(TreeItem parent, int pos1, int pos2) {
		if (pos1 == pos2)
			return;
		if (pos1 < 0 || pos2 < 0)
			throw new IllegalArgumentException("position < 0");
		if (pos1 >= parent.size() || pos2 >= parent.size()) {
			throw new IllegalArgumentException("position >= size");
		}
		TreeItem item1 = parent.getChild(pos1);
		TreeItem item2 = parent.getChild(pos2);
		//wstawienie na pos1
		parent.remove(pos1);
		parent.add(pos1, item2);
		//wstawienie na pos2
		parent.remove(pos2);
		parent.add(pos2, item1);
	}
	
	/**
	 * przesuwa element z pozycji o jedną pozycję w górę
	 * @param parent   przodek przesuwanego elementu
	 * @param position pozycja elementu przed przesuwaniem
	 */
	public void moveUp(TreeItem parent, int position) {
		if (position <= 0)
			return;
		replace(parent, position, position - 1);
	}
	
	/**
	 * przesuwa element z pozycji o jedną pozycję w dół
	 * @param parent   przodek przesuwanego elementu
	 * @param position pozycja elementu przed przesuwaniem
	 */
	public void moveDown(TreeItem parent, int position) {
		if (position >= parent.size() - 1)
			return;
		replace(parent, position, position + 1);
	}
	
	/**
	 * przesuwa element z pozycji o określoną liczbę pozycji
	 * @param parent   przodek przesuwanego elementu
	 * @param position pozycja elementu przed przesuwaniem
	 * @param step     liczba pozycji do przesunięcia (dodatnia - w dół, ujemna - w górę)
	 * @return nowa pozycja elementu
	 */
	public int move(TreeItem parent, int position, int step) {
		int targetPosition = position + step;
		if (targetPosition < 0)
			targetPosition = 0;
		if (targetPosition >= parent.size())
			targetPosition = parent.size() - 1;
		while (position < targetPosition) {
			moveDown(parent, position);
			position++;
		}
		while (position > targetPosition) {
			moveUp(parent, position);
			position--;
		}
		return targetPosition;
	}
	
	/**
	 * przesuwa element z pozycji do wybranego miejsca
	 * @param parent         przodek przesuwanego elementu
	 * @param position       pozycja elementu przed przesuwaniem
	 * @param targetPosition pozycja elemetnu po przesuwaniu
	 */
	public void moveTo(TreeItem parent, int position, int targetPosition) {
		if (targetPosition < 0)
			targetPosition = 0;
		if (targetPosition >= parent.size())
			targetPosition = parent.size() - 1;
		while (position < targetPosition) {
			moveDown(parent, position);
			position++;
		}
		while (position > targetPosition) {
			moveUp(parent, position);
			position--;
		}
	}
	
	public void moveToEnd(TreeItem parent, int position) {
		moveTo(parent, position, parent.size() - 1);
	}
	
	public void moveToBegining(TreeItem parent, int position) {
		moveTo(parent, position, 0);
	}
	
	//  OBCINANIE NIEDOZWOLONYCH ZNAKÓW
	
	/**
	 * obcięcie białych znaków na początku i na końcu, usunięcie niedozwolonych znaków
	 * @param content zawartość elementu
	 * @return zawartość z obciętymi znakami
	 */
	public String trimContent(String content) {
		final String WHITE_CHARS = " ";
		final String INVALID_CHARS = "{}[]\n\t";
		//usunięcie niedozwolonych znaków ze środka
		for (int i = 0; i < content.length(); i++) {
			if (isCharInSet(content.charAt(i), INVALID_CHARS)) {
				content = content.substring(0, i) + content.substring(i + 1);
				i--;
			}
		}
		//obcinanie białych znaków na początku
		while (content.length() > 0 && isCharInSet(content.charAt(0), WHITE_CHARS)) {
			content = content.substring(1);
		}
		//obcinanie białych znaków na końcu
		while (content.length() > 0 && isCharInSet(content.charAt(content.length() - 1), WHITE_CHARS)) {
			content = content.substring(0, content.length() - 1);
		}
		return content;
	}
	
	private boolean isCharInSet(char c, String set) {
		for (int i = 0; i < set.length(); i++) {
			if (set.charAt(i) == c)
				return true;
		}
		return false;
	}
	
	//  ZAZNACZANIE ELEMENTÓW
	
	public List<Integer> getSelectedItems() {
		return selectedPositions;
	}
	
	public int getSelectedItemsCount() {
		if (selectedPositions == null)
			return 0;
		return selectedPositions.size();
	}
	
	public boolean isSelectionMode() {
		if (selectedPositions == null)
			return false;
		return selectedPositions.size() > 0;
	}
	
	public void startSelectionMode() {
		selectedPositions = new ArrayList<>();
	}
	
	public void cancelSelectionMode() {
		selectedPositions = null;
	}
	
	public void setItemSelected(int position, boolean selectedState) {
		if (!isSelectionMode()) {
			startSelectionMode();
		}
		if (selectedState == true) {
			if (isItemSelected(position)) {
				return;
			} else {
				selectedPositions.add(position);
			}
		} else {
			if (isItemSelected(position)) {
				selectedPositions.remove(new Integer(position));
				if (selectedPositions.isEmpty()) {
					selectedPositions = null;
				}
			} else {
				return;
			}
		}
	}
	
	public boolean isItemSelected(int position) {
		for (Integer pos : selectedPositions) {
			if (pos.intValue() == position) {
				return true;
			}
		}
		return false;
	}
	
	public void toggleItemSelected(int position) {
		setItemSelected(position, !isItemSelected(position));
	}
	
	//  SCHOWEK
	
	public List<TreeItem> getClipboard() {
		return clipboard;
	}
	
	public int getClipboardSize() {
		if (clipboard == null)
			return 0;
		return clipboard.size();
	}
	
	public boolean isClipboardEmpty() {
		if (clipboard == null)
			return true;
		return clipboard.size() == 0;
	}
	
	public void clearClipboard() {
		clipboard = null;
	}
	
	public void addToClipboard(TreeItem item) {
		if (clipboard == null) {
			clipboard = new ArrayList<>();
		}
		clipboard.add(new TreeItem(item));
	}
	
	public void recopyClipboard() {
		if (clipboard != null) {
			ArrayList<TreeItem> newClipboard = new ArrayList<>();
			for (TreeItem item : clipboard) {
				newClipboard.add(new TreeItem(item));
			}
			clipboard = newClipboard;
		}
	}
	
	//  Zapamiętywanie pozycji scrolla
	
	public void storeScrollPosition(TreeItem item, Integer y) {
		if (item != null && y != null) {
			storedScrollPositions.put(item, y);
		}
	}
	
	public Integer restoreScrollPosition(TreeItem item) {
		return storedScrollPositions.get(item);
	}
	
	// sumowanie wartości pozycji
	
	public BigDecimal sumSelected() throws NumberFormatException {
		BigDecimal sum = new BigDecimal(0);
		for (Integer selectedPos : selectedPositions) {
			TreeItem selectedItem = getCurrentItem().getChild(selectedPos);
			BigDecimal itemValue = getItemNumericValue(selectedItem.getContent());
			if (itemValue != null) {
				sum = sum.add(itemValue);
			}
		}
		return sum;
	}
	
	private BigDecimal getItemNumericValue(String content) throws NumberFormatException {
		
		String valueStr = null;
		
		content = content.replace(',', '.');
		
		Pattern pattern = Pattern.compile("^(-? ?\\d+(\\.\\d+)?)(.*?)$");
		Matcher matcher = pattern.matcher(content);
		if (matcher.matches()) {
			valueStr = matcher.group(1);
			valueStr = valueStr.replaceAll(" ", "");
		} else {
			pattern = Pattern.compile("^(.*?)(-? ?\\d+(\\.\\d+)?)$");
			matcher = pattern.matcher(content);
			if (matcher.matches()) {
				valueStr = matcher.group(2);
				valueStr = valueStr.replaceAll(" ", "");
			}
		}
		
		if (valueStr != null) {
			try {
				BigDecimal numeric = new BigDecimal(valueStr);
				return numeric;
			} catch (NumberFormatException e) {
				throw new NumberFormatException("Nieprawidłowy format liczby:\n" + valueStr);
			}
		} else {
			throw new NumberFormatException("Brak wartości liczbowej:\n" + content);
		}
	}
}