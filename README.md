# Tugas Information Retrieval 

| NPM | Nama Anggota | Peran | Tugas Utama |
|---|---|---|---|
| 6182301013 | Bryan Ricaldi | Data & Indexing Engineer | Text preprocessing & membangun Inverted Index |
| isi npm | Robert Saputra | Boolean Engine Developer | Query parser & eksekusi logika Boolean |
| isi npm | Bima Rahmadani | Tolerant Retrieval Specialist| Menangani wildcard & spelling correction |

# Dataset
Berikut merupakan dataset yang kami gunakan
```
https://www.kaggle.com/datasets/hhhoang/cranfield-dataset?resource=download
```
# Cara Menjalankan Program

1. Masuk ke dalam folder `src` dengan perintah berikut:
   ```bash
   cd src
   ```
   **Note:** Program harus dijalankan menggunakan terminal dikarenakan jika menggunakan auto run dari vscode path dataset akan berbeda
   
2. Compile dan run file Main.java:
   ```bash
   javac Main.java
   java Main

3. Masukan query yang di inginkan menggunakan operator boolean (AND, OR, NOT):  
   Contoh:
   ```
   Query: drag AND calculations
   Hasil: [147, 163, 179, 188, 227, 235, 246, 248, 263, 279]
   ```
   **Note:** Penggunaan huruf kapital atau tidak dalam penulisan operator boolean tidak berpengaruh pada hasil pencarian.
  
