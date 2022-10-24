# Exec-it
It's a simple shell which working on Linux,  Windows, Mac OS, and Solaris
rócona dokumentacja/instrukcja programu Exec-it (PL)

======================================================

Instrukcja uruchomienia:



1. Pobieramy najnowszą wersję Java SE

2. Pobieramy powłokę dostępną pod linkiem "Pobierz najnowszą wersję stable"

3. Wchodzimy w powłokę naszego systemu

4. Wpisujemy java -jar [lokalizacja i nazwa pliku .jar]



======================================================

Znaki specjalne:



Aby wpisać komendę programu musimy poprzedzić ją "!" (wykrzyknikiem)

np. !print , !echo itp.



Aby wpisać komendę powłoki danego systemu (Windows - cmd, pozostałe - BASH)

musimy poprzedzić ją "$" (znakiem dolara), po wpisaniu $ bez komendy przenosimy się do 

powłoki danego systemu operacyjnego.



Aby uruchomić program wpisujemy jego nazwę bez ścieżki np. notepad, 

lub ze ścieżką absolutną C:\Program Files\Google\chrome.exe, 

lub ze ścieżką relatywną ~\home\test.sh



======================================================

Lokalizacje plików / folderów:



W powłoce exec-it typy ukośników oraz ich ilość nie mają znaczenia , zostaną one

i tak poprawione do danego systemu np.



W systemie Windows: 

Wpisanie ścieżki C:/Users\\\ktos/Desktop zostanie przetworzone na C:\Users\ktos\Desktop

W systemie Linux/Unix/Mac OS: 

Wpisanie ścieżki \root/folder/example// zostanie przetworzone na /root/folder/example



Znaki specjalne ścieżek:

Można je stosować w dowolnych ścieżkach podawanych w komendach programu exec-it 



~ katalog domowy , w linuxie /home/[nazwa] , w windowsie C:\Users\[nazwa]

* stosowane przed ścieżką oznacza że dana ścieżka jest absolutna

# lokalizacja pliku programu. jar z którego została uruchomiona powłoka



======================================================

Dostępne komendy/polecenia (użytkowe oraz działające):



exit,end,bye - wyłączają powłokę

help,man - wyświetla ten plik pomocy (jeszcze nie zaimplementowane)

echo [param] - bez parametru wyświelta stan dodatkowych informacji (echo) ,

	z parametrem (on/off) włącza lub wyłącza stan echo.
cd,change_directory [path1] - przechodzi do [path1]

copy,cp [path1] [path2] - kopiuje plik lub folder(może nie działać prawidłowo) z path1 do path2   

pwd [path1] - bez parametru wyświetla obecną lokalizacje ,z parametrem pokazuje pełną ścieżke do path1 

	nawet jeśli ten folder / plik nie istnieje







======================================================

Informacje ogólne , ciekawostki, offtopic:

-Program powłoki exec it napisany przez atomwoz`a ma około 3875 linijek kodu + około 5000 linijek zewnętrznych bibliotek (nie Javy).



-Powłoka powstała 12.12.2020



-Program używa dwóch blibliotek zewnętrznych MathParaser 

do obsługi wyrażeń matematycznych oraz Jline 2.0 oraz 3.0

do obsługi menu wyboru i kolorów tekstu.
