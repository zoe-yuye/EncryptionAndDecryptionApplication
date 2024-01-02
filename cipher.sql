-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jan 02, 2024 at 01:14 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `cipher`
--

-- --------------------------------------------------------

--
-- Table structure for table `keys`
--

CREATE TABLE `keys` (
  `name` varchar(200) NOT NULL,
  `text` longtext DEFAULT NULL,
  `key` longtext DEFAULT NULL,
  `userId` int(11) NOT NULL,
  `algorithm` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `keys`
--

INSERT INTO `keys` (`name`, `text`, `key`, `userId`, `algorithm`) VALUES
('key2', 'RonDKXjmJPg=', 'cGhoighw+AE=', 1, 'DES Cipher'),
('key1', '1Y37NkLEvggF9Ya/N9UYOw==', '25EOWRTOK6crYiWaQw0P7F9//lLiQe76oH2Rmfi38Xs=', 1, 'AES Cipher'),
('hello', 'bc+dj4PRiMA=', 'koWJ5Xx1kXw=', 1, 'DES Cipher'),
('hi', '/RrwUjnPaXyPnBwQV1pZmg==', '', 1, 'AES Cipher'),
('haha', '', 'WMo9doBv80L07kgOB9Ah7efXkzFA++rUYU31PQCkr0E=', 1, 'AES Cipher'),
('test', 'C2rxMAN9UYU=', '2UmRg4kyDeo=', 1, 'DES Cipher'),
('test4', '6vUS0FkUFOI=', 'wTtkjIU+nf4=', 1, 'DES Cipher'),
('123', '2jwCyJvbXF4=', '{vrk;\0~[>r^F', 1, 'DES Cipher'),
('syx', '4ABV4IrbInA=', '~bVKAz<Z@QVF', 1, 'DES Cipher'),
('sss', 'E4EogxuEKs0=', 'E}L7H:umfS7@', 1, 'DES Cipher'),
('1122', 'Sp5/2xs+gac=', 'XpHfUdfo\\ij@', 1, 'DES Cipher'),
('111222', 'xD67iZ0hoFE=', '793Rs6ow7|;@', 1, 'DES Cipher'),
('omg', 'PsOE3WjQRKE=', '\\|R=ky::Q%NF', 1, 'DES Cipher'),
('abc', 'ORt3rFgYcEMmubZepuWJlg==', 'B_AY>8_nY\\;%a{j\\zm9z8bpX4j}oLJOU|MB>Z4s9!WlF', 1, 'AES Cipher'),
('abcc', 'ZWy8bAT0w7Q=', 'nnjOZR4\":[tF', 1, 'DES Cipher'),
('123', 'QUxw+v1V0CQ=', 'j8S%@OPTLS9F', 1, 'DES Cipher'),
('1999', '9Z1rndSIB+o=', 'X<a]#NL;?mJF', 1, 'DES Cipher');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(200) NOT NULL,
  `password` varchar(200) NOT NULL,
  `theme` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `theme`) VALUES
(1, '111', 'III', 'theme2'),
(2, '222', 'JJJ', 'theme2'),
(3, '333', 'KKK', 'theme3'),
(4, '444', 'LLL', 'theme4'),
(5, '555', 'MMM', 'theme2'),
(6, '666', 'NNN', 'theme2'),
(7, '777', 'OOO', 'theme2');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
