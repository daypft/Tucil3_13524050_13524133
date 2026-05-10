### Tucil3_13524050_13524133

---

<div align="center" id="contributor">
  <strong>
    <table align="center">
      <tr align="center">
        <td>NIM</td>
        <td>Nama</td>
      </tr>
      <tr align="center">
        <td>13524050</td>
        <td>Raysha Erviandika Putra</td>
      </tr>
      <tr align="center">
        <td>13524133</td>
        <td>Muhammad Daffa Arrizki Yanma</td>
      </tr>
    </table>
  </strong>
</div>

## Program Description

Ice Sliding Puzzle adalah permainan logika di mana pemain harus menggerakkan karakter dari titik awal menuju titik keluar di atas permukaan es yang licin. Pin atau karakter hanya dapat bergerak secara horizontal atau vertikal, namun karena permukaan licin, karakter tidak akan berhenti bergerak sampai menabrak dinding atau rintangan.

Program ini dibuat untuk menyelesaikan permasalahan Ice Sliding Puzzle. Pada papan, pemain berangkat dari titik awal `Z` dan harus mencapai titik tujuan `O`. Setiap petak memiliki bobot atau cost tertentu, sehingga rute yang dipilih dapat bergantung pada jumlah gerakan dan total cost yang dilalui.

Selain mencari rute ke tujuan, program juga menangani beberapa aturan khusus pada peta, seperti Petak `X` berfungsi sebagai dinding yang tidak dapat dilewati, sedangkan petak bernomor `0-9` harus dilewati sesuai urutan. Pergerakan pemain dilakukan ke salah satu dari empat arah, yaitu atas, bawah, kiri, atau kanan, lalu pemain akan terus bergerak sampai terhalang sesuai aturan peta.

Program juga menyediakan visualisasi proses pathfinding menggunakan JavaFX. Pengguna dapat memilih algoritma yang digunakan untuk mencari jalur, melihat hasil rute, total cost, jumlah iterasi, waktu eksekusi, dan log pencarian. Algoritma yang tersedia adalah:

- Uniform Cost Search (UCS)
- Greedy Best-First Search (GBFS)
- A\* Search

## Installation & Setup

### Requirements

- Java Development Kit (JDK) 17 atau lebih baru
- Apache Maven
- IDE pilihan

### Installing Dependencies

#### Windows

1. Java JDK  
   [Download JDK](https://www.oracle.com/asean/java/technologies/downloads/)

2. Maven  
   [Download Maven](https://maven.apache.org/download.cgi)

## How to Run

Clone repository:

```bash
git clone https://github.com/daypft/Tucil3_13524050_13524133.git
```

Masuk ke direktori proyek:

```bash
cd Tucil3_13524050_13524133
```

```bash
mvn javafx:run
```
