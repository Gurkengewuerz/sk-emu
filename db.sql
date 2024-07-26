-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: db
-- Erstellungszeit: 26. Jul 2024 um 10:23
-- Server-Version: 10.11.7-MariaDB-1:10.11.7+maria~ubu2204
-- PHP-Version: 8.2.19

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Datenbank: `db`
--

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `messreihe`
--

CREATE TABLE `messreihe` (
  `messreihenId` int(11) NOT NULL,
  `zeitIntervall` int(11) NOT NULL,
  `verbraucher` varchar(255) NOT NULL,
  `messgroesse` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Daten für Tabelle `messreihe`
--

INSERT INTO `messreihe` (`messreihenId`, `zeitIntervall`, `verbraucher`, `messgroesse`) VALUES
(10, 5, 'Kühlschrank', 'Arbeit'),
(11, 5, 'LED', 'Leistung'),
(12, 10, 'Smartphone', 'Leistung'),
(13, 5, 'Laptop', 'Leistung'),
(14, 5, 'Laptop', 'Leistung');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `messung`
--

CREATE TABLE `messung` (
  `laufendeNummer` int(11) NOT NULL,
  `wert` decimal(20,6) NOT NULL,
  `timeMillis` bigint(20) NOT NULL,
  `messreihenId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Daten für Tabelle `messung`
--

INSERT INTO `messung` (`laufendeNummer`, `wert`, `timeMillis`, `messreihenId`) VALUES
(0, 46.800000, 1716371625413, 13),
(0, 11.700000, 1716373515536, 14),
(1, 0.437000, 0, 10),
(1, 10.000000, 0, 12),
(1, 46.900000, 1716371635810, 13),
(1, 19.800000, 1716373524675, 14),
(2, 0.437000, 0, 10),
(2, 5.700000, 1713363043318, 12),
(3, 10.000000, 0, 10),
(3, 0.000000, 1713363076767, 12);

--
-- Indizes der exportierten Tabellen
--

--
-- Indizes für die Tabelle `messreihe`
--
ALTER TABLE `messreihe`
  ADD PRIMARY KEY (`messreihenId`);

--
-- Indizes für die Tabelle `messung`
--
ALTER TABLE `messung`
  ADD PRIMARY KEY (`laufendeNummer`,`messreihenId`),
  ADD KEY `messreihenId` (`messreihenId`);

--
-- AUTO_INCREMENT für exportierte Tabellen
--

--
-- AUTO_INCREMENT für Tabelle `messreihe`
--
ALTER TABLE `messreihe`
  MODIFY `messreihenId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- Constraints der exportierten Tabellen
--

--
-- Constraints der Tabelle `messung`
--
ALTER TABLE `messung`
  ADD CONSTRAINT `messung_ibfk_1` FOREIGN KEY (`messreihenId`) REFERENCES `messreihe` (`messreihenId`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
