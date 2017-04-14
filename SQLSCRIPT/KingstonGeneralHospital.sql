-- phpMyAdmin SQL Dump
-- version 4.5.2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Apr 11, 2017 at 03:15 AM
-- Server version: 10.1.13-MariaDB
-- PHP Version: 7.0.8

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `KingstonGeneralHospital`
--

-- --------------------------------------------------------

--
-- Table structure for table `ClickedLinks`
--

CREATE TABLE `ClickedLinks` (
  `SearchID` int(11) NOT NULL,
  `Link` varchar(200) NOT NULL,
  `TimeSpent` int(10) NOT NULL,
  `Depth` int(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `SearchQueries`
--

CREATE TABLE `SearchQueries` (
  `SearchID` int(11) NOT NULL,
  `SearchedBy` varchar(200) NOT NULL,
  `Query` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `SearchResults`
--

CREATE TABLE `SearchResults` (
  `SearchID` int(11) NOT NULL,
  `SearchResult` varchar(200) NOT NULL,
  `Rank` int(2) NOT NULL,
  `Rating` int(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Users`
--

CREATE TABLE `Users` (
  `Email` varchar(100) NOT NULL,
  `Password` varchar(20) NOT NULL,
  `Age` int(2) NOT NULL,
  `Sex` varchar(1) NOT NULL,
  `Discipline` varchar(20) NOT NULL,
  `Experience` int(2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `ClickedLinks`
--
ALTER TABLE `ClickedLinks`
  ADD KEY `SearchID` (`SearchID`);

--
-- Indexes for table `SearchQueries`
--
ALTER TABLE `SearchQueries`
  ADD PRIMARY KEY (`SearchID`),
  ADD KEY `SearchedBy` (`SearchedBy`);

--
-- Indexes for table `SearchResults`
--
ALTER TABLE `SearchResults`
  ADD KEY `SearchID` (`SearchID`),
  ADD KEY `SearchID_2` (`SearchID`);

--
-- Indexes for table `Users`
--
ALTER TABLE `Users`
  ADD PRIMARY KEY (`Email`),
  ADD UNIQUE KEY `Email` (`Email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `SearchQueries`
--
ALTER TABLE `SearchQueries`
  MODIFY `SearchID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=225;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `ClickedLinks`
--
ALTER TABLE `ClickedLinks`
  ADD CONSTRAINT `ClickedLinks_ibfk_1` FOREIGN KEY (`SearchID`) REFERENCES `SearchQueries` (`SearchID`);

--
-- Constraints for table `SearchQueries`
--
ALTER TABLE `SearchQueries`
  ADD CONSTRAINT `SearchQueries_ibfk_1` FOREIGN KEY (`SearchedBy`) REFERENCES `Users` (`Email`);

--
-- Constraints for table `SearchResults`
--
ALTER TABLE `SearchResults`
  ADD CONSTRAINT `SearchResults_ibfk_1` FOREIGN KEY (`SearchID`) REFERENCES `SearchQueries` (`SearchID`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
