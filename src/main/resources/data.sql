---- Inserting data into admins table
--INSERT INTO `admins` (`id`, `accountNonExpired`, `accountNonLocked`, `credentialsNonExpired`, `email`, `enabled`, `password`, `username`) VALUES
--(1, b'1', b'1', b'1', 'admin@stemapp.com', b'1', '$2a$10$XptfskLsT1l/bRTLRiiCgejHqOpgXFreUnNUa35gJdCr2v2QbVFzu', 'admin'),
--(2, b'1', b'1', b'1', 'moderator@stemapp.com', b'1', '$2a$10$XptfskLsT1l/bRTLRiiCgejHqOpgXFreUnNUa35gJdCr2v2QbVFzu', 'moderator'),
--(3, b'1', b'1', b'1', 'editor@stemapp.com', b'1', '$2a$10$XptfskLsT1l/bRTLRiiCgejHqOpgXFreUnNUa35gJdCr2v2QbVFzu', 'editor');
--
---- Inserting data into admin_roles table
--INSERT INTO `admin_roles` (`admin_id`, `roles`) VALUES
--(1, 'ROLE_ADMIN'),
--(1, 'ROLE_MODERATOR'),
--(1, 'ROLE_EDITOR'),
--(2, 'ROLE_MODERATOR'),
--(3, 'ROLE_EDITOR');
--
---- Inserting data into categories table (including 'All' category)
--INSERT INTO `categories` (`id`, `name`) VALUES
--('all', 'All'),
--('stem', 'STEM Education'),
--('laboratories', 'Laboratories'),
--('teacher', 'Teacher Training'),
--('community', 'Community Engagement'),
--('science', 'Science'),
--('technology', 'Technology'),
--('engineering', 'Engineering'),
--('mathematics', 'Mathematics');
--
---- Inserting data into authors table with profile pictures
--INSERT INTO `authors` (`id`, `image`, `initials`, `name`) VALUES
--(1, 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2', 'RM', 'Dr. Rose E. Matete'),
--(2, 'https://images.unsplash.com/photo-1560250097-0b93528c311a', 'OW', 'Dr. Ombeni W. Msuya'),
--(3, 'https://images.unsplash.com/photo-1542190891-2093d38760f2', 'AJ', 'Dr. Abdallah J. Seni'),
--(4, 'https://images.unsplash.com/photo-1544005313-94ddf0286df2', 'CE', 'Christopher Emily'),
--(5, 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d', 'AK', 'Andrew Keha Kuko'),
--(6, 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2', 'JD', 'John Doe'),
--(7, 'https://images.unsplash.com/photo-1580489944761-15a19d654956', 'AS', 'Alice Smith'),
--(8, 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d', 'RJ', 'Robert Johnson');
--
---- Inserting data into blog_posts table with provided image URLs
--INSERT INTO `blog_posts` (`id`, `alt`, `content`, `createdAt`, `document_url`, `image`, `comments`, `likes`, `title`, `updatedAt`, `author_id`, `category_id`) VALUES
--(1, 'STEM Classroom', 'Tanzania''s future depends on developing strong STEM capabilities in its youth. This article explores the current challenges and opportunities in building robust STEM education programs across secondary schools in Tanzania.', '2022-11-10 08:00:00.000000', 'documents/stem-capacity-tz.pdf', 'https://afrikahayat.org/wp-content/uploads/2023/12/cambodia-student-reads-class.jpg', 8, 24, 'Building STEM Capacity in Tanzania''s Secondary Schools', '2022-11-10 08:00:00.000000', 1, 'stem'),
--(2, 'School Laboratory', 'Practical laboratory experience is crucial for science education, but many Tanzanian schools face resource constraints. Learn how schools are creating innovative laboratory solutions with minimal resources.', '2022-11-05 09:30:00.000000', 'documents/school-laboratories.pdf', 'https://images.squarespace-cdn.com/content/v1/6037c51efd013540f2cb8756/1648654975736-FOIRDAT938BO8EKLTD3R/Milembe+girls+at+lab+slider.jpg?format=1000w', 5, 18, 'Designing Effective School Laboratories with Limited Resources', '2022-11-05 09:30:00.000000', 2, 'laboratories'),
--(3, 'Teacher Training', 'Well-trained teachers are the backbone of effective science education. This article discusses modern approaches to professional development for science teachers in Tanzania''s secondary schools.', '2022-10-28 10:15:00.000000', 'documents/teacher-training.pdf', 'https://staticsb.we.org/f/52095/1152x640/c29fe47cc4/tanzania-carousel-9.jpg', 12, 32, 'Empowering Science Teachers: Professional Development Approaches', '2022-10-28 10:15:00.000000', 3, 'teacher'),
--(4, 'Community Education', 'Community involvement is essential for sustainable education improvement. Discover effective strategies for engaging parents and community members in supporting STEM education initiatives in Tanzania.', '2022-10-21 11:00:00.000000', 'documents/community-engagement.pdf', 'https://lsi.fsu.edu/sites/g/files/upcbnu1926/files/2024-04/IMGP2617-2.jpg', 9, 27, 'Engaging Parents and Communities in STEM Education', '2022-10-21 11:00:00.000000', 4, 'community'),
--(5, 'Women in Science', 'Gender disparities persist in STEM fields across Tanzania. This article explores innovative approaches to encourage more female students to pursue science and mathematics education.', '2022-10-14 13:45:00.000000', 'documents/women-in-stem.pdf', 'https://africa.unwomen.org/sites/default/files/2023-05/IMG_0329%20%281%29_1.JPG', 16, 41, 'Breaking Barriers: Increasing Female Participation in STEM Fields', '2022-10-14 13:45:00.000000', 5, 'stem'),
--(6, 'Quantum Computing', 'Exploring the fundamentals of quantum computing and its potential applications in solving complex problems.', '2023-01-15 09:30:00.000000', 'documents/quantum-computing.pdf', 'https://images.unsplash.com/photo-1635070041078-e363dbe005cb', 14, 56, 'Introduction to Quantum Computing', '2023-01-20 11:45:00.000000', 6, 'technology'),
--(7, 'Sustainable Engineering', 'Innovative engineering solutions for sustainable development in African contexts.', '2023-02-10 14:15:00.000000', 'documents/sustainable-engineering.pdf', 'https://images.unsplash.com/photo-1605152276897-4f618f831968', 7, 38, 'Sustainable Engineering Practices', '2023-02-12 16:30:00.000000', 7, 'engineering'),
--(8, 'Advanced Mathematics', 'New approaches to teaching complex mathematical concepts in secondary education.', '2023-03-05 10:00:00.000000', 'documents/advanced-math.pdf', 'https://images.unsplash.com/photo-1635070041078-e363dbe005cb', 11, 42, 'Modern Mathematics Education', '2023-03-08 09:15:00.000000', 8, 'mathematics');
--
---- Inserting data into subscriptions table
--INSERT INTO `subscriptions` (`id`, `email`, `is_active`, `subscribed_at`) VALUES
--(1, 'john@gmail.com', b'1', '2025-04-25 13:03:35.000000'),
--(2, 'haroon@gmail.com', b'1', '2025-04-25 13:16:39.000000'),
--(3, 'alice@example.com', b'1', '2025-04-26 10:15:22.000000'),
--(4, 'bob@example.com', b'1', '2025-04-26 10:16:45.000000'),
--(5, 'carol@example.com', b'1', '2025-04-26 10:18:03.000000'),
--(6, 'dave@example.com', b'1', '2025-04-26 10:19:30.000000'),
--(7, 'eve@example.com', b'1', '2025-04-26 10:20:15.000000'),
--(8, 'frank@example.com', b'0', '2025-04-25 14:22:40.000000'),
--(9, 'grace@example.com', b'1', '2025-04-26 10:25:55.000000'),
--(10, 'henry@example.com', b'1', '2025-04-26 10:27:10.000000'),
--(11, 'irene@example.com', b'0', '2025-04-24 09:30:45.000000'),
--(12, 'jack@example.com', b'1', '2025-04-26 10:30:20.000000');







--
--    -- Inserting data into admins table (only SUPER_ADMIN and ADMIN)
--    INSERT INTO `admins` (`id`, `accountNonExpired`, `accountNonLocked`, `credentialsNonExpired`, `email`, `enabled`, `password`, `username`) VALUES
--    (1, b'1', b'1', b'1', 'superadmin@stemapp.com', b'1', '$2a$10$tByEQwKElelaryoIfXv20./BQa7YLEFycaUVVmbVeLbN/vjqpSB1S', 'superadmin'),
--    (2, b'1', b'1', b'1', 'admin@stemapp.com', b'1', '$2a$10$XptfskLsT1l/bRTLRiiCgejHqOpgXFreUnNUa35gJdCr2v2QbVFzu', 'admin');
--
--    -- Inserting data into admin_roles table
--    INSERT INTO `admin_roles` (`admin_id`, `roles`) VALUES
--    (1, 'ROLE_SUPER_ADMIN'),
--    (2, 'ROLE_ADMIN');
--
--    -- Inserting data into categories table (including 'All' category)
--    INSERT INTO `categories` (`id`, `name`) VALUES
--    ('all', 'All'),
--    ('stem', 'STEM Education'),
--    ('laboratories', 'Laboratories'),
--    ('teacher', 'Teacher Training'),
--    ('community', 'Community Engagement'),
--    ('science', 'Science'),
--    ('technology', 'Technology'),
--    ('engineering', 'Engineering'),
--    ('mathematics', 'Mathematics');
--
--    -- Inserting data into authors table with profile pictures
--    INSERT INTO `authors` (`id`, `image`, `initials`, `name`) VALUES
--    (1, 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2', 'RM', 'Dr. Rose E. Matete'),
--    (2, 'https://images.unsplash.com/photo-1560250097-0b93528c311a', 'OW', 'Dr. Ombeni W. Msuya'),
--    (3, 'https://images.unsplash.com/photo-1542190891-2093d38760f2', 'AJ', 'Dr. Abdallah J. Seni'),
--    (4, 'https://images.unsplash.com/photo-1544005313-94ddf0286df2', 'CE', 'Christopher Emily'),
--    (5, 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d', 'AK', 'Andrew Keha Kuko'),
--    (6, 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2', 'JD', 'John Doe'),
--    (7, 'https://images.unsplash.com/photo-1580489944761-15a19d654956', 'AS', 'Alice Smith'),
--    (8, 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d', 'RJ', 'Robert Johnson');
--
--    -- Inserting data into blog_posts table with provided image URLs
--    INSERT INTO `blog_posts` (`id`, `alt`, `content`, `createdAt`, `document_url`, `image`, `comments`, `likes`, `title`, `updatedAt`, `author_id`, `category_id`) VALUES
--    (1, 'STEM Classroom', 'Tanzania''s future depends on developing strong STEM capabilities in its youth. This article explores the current challenges and opportunities in building robust STEM education programs across secondary schools in Tanzania.', '2022-11-10 08:00:00.000000', 'documents/stem-capacity-tz.pdf', 'https://afrikahayat.org/wp-content/uploads/2023/12/cambodia-student-reads-class.jpg', 8, 24, 'Building STEM Capacity in Tanzania''s Secondary Schools', '2022-11-10 08:00:00.000000', 1, 'stem'),
--    (2, 'School Laboratory', 'Practical laboratory experience is crucial for science education, but many Tanzanian schools face resource constraints. Learn how schools are creating innovative laboratory solutions with minimal resources.', '2022-11-05 09:30:00.000000', 'documents/school-laboratories.pdf', 'https://images.squarespace-cdn.com/content/v1/6037c51efd013540f2cb8756/1648654975736-FOIRDAT938BO8EKLTD3R/Milembe+girls+at+lab+slider.jpg?format=1000w', 5, 18, 'Designing Effective School Laboratories with Limited Resources', '2022-11-05 09:30:00.000000', 2, 'laboratories'),
--    (3, 'Teacher Training', 'Well-trained teachers are the backbone of effective science education. This article discusses modern approaches to professional development for science teachers in Tanzania''s secondary schools.', '2022-10-28 10:15:00.000000', 'documents/teacher-training.pdf', 'https://staticsb.we.org/f/52095/1152x640/c29fe47cc4/tanzania-carousel-9.jpg', 12, 32, 'Empowering Science Teachers: Professional Development Approaches', '2022-10-28 10:15:00.000000', 3, 'teacher'),
--    (4, 'Community Education', 'Community involvement is essential for sustainable education improvement. Discover effective strategies for engaging parents and community members in supporting STEM education initiatives in Tanzania.', '2022-10-21 11:00:00.000000', 'documents/community-engagement.pdf', 'https://lsi.fsu.edu/sites/g/files/upcbnu1926/files/2024-04/IMGP2617-2.jpg', 9, 27, 'Engaging Parents and Communities in STEM Education', '2022-10-21 11:00:00.000000', 4, 'community'),
--    (5, 'Women in Science', 'Gender disparities persist in STEM fields across Tanzania. This article explores innovative approaches to encourage more female students to pursue science and mathematics education.', '2022-10-14 13:45:00.000000', 'documents/women-in-stem.pdf', 'https://africa.unwomen.org/sites/default/files/2023-05/IMG_0329%20%281%29_1.JPG', 16, 41, 'Breaking Barriers: Increasing Female Participation in STEM Fields', '2022-10-14 13:45:00.000000', 5, 'stem'),
--    (6, 'Quantum Computing', 'Exploring the fundamentals of quantum computing and its potential applications in solving complex problems.', '2023-01-15 09:30:00.000000', 'documents/quantum-computing.pdf', 'https://images.unsplash.com/photo-1635070041078-e363dbe005cb', 14, 56, 'Introduction to Quantum Computing', '2023-01-20 11:45:00.000000', 6, 'technology'),
--    (7, 'Sustainable Engineering', 'Innovative engineering solutions for sustainable development in African contexts.', '2023-02-10 14:15:00.000000', 'documents/sustainable-engineering.pdf', 'https://images.unsplash.com/photo-1605152276897-4f618f831968', 7, 38, 'Sustainable Engineering Practices', '2023-02-12 16:30:00.000000', 7, 'engineering'),
--    (8, 'Advanced Mathematics', 'New approaches to teaching complex mathematical concepts in secondary education.', '2023-03-05 10:00:00.000000', 'documents/advanced-math.pdf', 'https://images.unsplash.com/photo-1635070041078-e363dbe005cb', 11, 42, 'Modern Mathematics Education', '2023-03-08 09:15:00.000000', 8, 'mathematics');
--
--    -- Inserting data into subscriptions table
--    INSERT INTO `subscriptions` (`id`, `email`, `is_active`, `subscribed_at`) VALUES
--    (1, 'john@gmail.com', b'1', '2025-04-25 13:03:35.000000'),
--    (2, 'haroon@gmail.com', b'1', '2025-04-25 13:16:39.000000'),
--    (3, 'alice@example.com', b'1', '2025-04-26 10:15:22.000000'),
--    (4, 'bob@example.com', b'1', '2025-04-26 10:16:45.000000'),
--    (5, 'carol@example.com', b'1', '2025-04-26 10:18:03.000000'),
--    (6, 'dave@example.com', b'1', '2025-04-26 10:19:30.000000'),
--    (7, 'eve@example.com', b'1', '2025-04-26 10:20:15.000000'),
--    (8, 'frank@example.com', b'0', '2025-04-25 14:22:40.000000'),
--    (9, 'grace@example.com', b'1', '2025-04-26 10:25:55.000000'),
--    (10, 'henry@example.com', b'1', '2025-04-26 10:27:10.000000'),
--    (11, 'irene@example.com', b'0', '2025-04-24 09:30:45.000000'),
--    (12, 'jack@example.com', b'1', '2025-04-26 10:30:20.000000');











-- phpMyAdmin SQL Dump
-- version 5.2.1deb1
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: May 23, 2025 at 11:07 PM
-- Server version: 10.11.11-MariaDB-0+deb12u1
-- PHP Version: 8.2.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `Stemdb2`
--

-- --------------------------------------------------------

--
-- Dumping data for table `roles`
--

INSERT INTO `roles` (`Id`, `name`) VALUES
(2, 'ROLE_ADMIN'),
(3, 'ROLE_USER');

-- --------------------------------------------------------

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`id`, `name`) VALUES
('stem', 'STEM Education'),
('laboratories', 'Laboratories'),
('teacher', 'Teacher Training'),
('community', 'Community Engagement'),
('science', 'Science'),
('technology', 'Technology'),
('engineering', 'Engineering'),
('mathematics', 'Mathematics');

-- --------------------------------------------------------

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `address`, `approved`, `bio`, `birthdate`, `department`, `education`, `email`, `lastLogin`, `name`, `notificationAppEnabled`, `notificationEmailEnabled`, `notificationUpdatesEnabled`, `occupation`, `password`, `phone`, `profilePictureUrl`, `securitySessionTimeout`, `securityTwoFactorEnabled`, `username`) VALUES
(2, NULL, b'1', NULL, NULL, 'ADMIN', NULL, 'admin@stemapp.com', '2025-05-23 20:00:00.000000', 'Administrator', b'1', b'1', b'1', NULL, '$2a$10$XptfskLsT1l/bRTLRiiCgejHqOpgXFreUnNUa35gJdCr2v2QbVFzu', NULL, NULL, '30', b'1', 'admin'),
(3, '123 Academic Rd, Dar es Salaam', b'1', 'Expert in STEM education', '1975-03-10', 'Education', 'PhD Education', 'rose.matete@stemapp.com', '2025-05-23 18:00:00.000000', 'Dr. Rose E. Matete', b'1', b'1', b'0', 'Professor', '$2a$10$abc123xyz789', '+255700123456', 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2', '15', b'0', 'rmatete'),
(4, '456 Science St, Arusha', b'1', 'Researcher in laboratory design', '1980-06-15', 'Science', 'PhD Chemistry', 'ombeni.msuya@stemapp.com', '2025-05-23 17:30:00.000000', 'Dr. Ombeni W. Msuya', b'0', b'1', b'1', 'Researcher', '$2a$10$xyz789abc123', '+255711987654', 'https://images.unsplash.com/photo-1560250097-0b93528c311a', '15', b'0', 'omsuya'),
(5, '789 Tech Ave, Dodoma', b'1', 'Teacher training specialist', '1978-09-20', 'Education', 'PhD Education', 'abdallah.seni@stemapp.com', '2025-05-23 16:00:00.000000', 'Dr. Abdallah J. Seni', b'1', b'0', b'1', 'Educator', '$2a$10$def456ghi789', '+255722345678', 'https://images.unsplash.com/photo-1542190891-2093d38760f2', '20', b'1', 'aseni'),
(6, '101 Community Rd, Zanzibar', b'1', 'Community engagement advocate', '1985-12-05', 'Social Work', 'MSc Sociology', 'christopher.emily@stemapp.com', '2025-05-23 15:00:00.000000', 'Christopher Emily', b'0', b'1', b'0', 'Community Organizer', '$2a$10$jkl789mno123', '+255733456789', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2', '10', b'0', 'cemily'),
(7, '202 STEM Ln, Mwanza', b'1', 'Promoting women in STEM', '1990-04-25', 'Education', 'MSc Gender Studies', 'andrew.keha@stemapp.com', '2025-05-23 14:30:00.000000', 'Andrew Keha Kuko', b'1', b'1', b'1', 'Program Coordinator', '$2a$10$pqr123stu789', '+255744567890', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d', '15', b'1', 'akeha'),
(8, '303 Tech Park, Dar es Salaam', b'1', 'Quantum computing enthusiast', '1982-07-30', 'Technology', 'PhD Computer Science', 'john.doe@stemapp.com', '2025-05-23 13:00:00.000000', 'John Doe', b'0', b'1', b'0', 'Tech Researcher', '$2a$10$vwx789yz123', '+255755678901', 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2', '20', b'0', 'jdoe'),
(9, '404 Engineering Blvd, Arusha', b'1', 'Sustainable engineering expert', '1988-11-15', 'Engineering', 'PhD Civil Engineering', 'alice.smith@stemapp.com', '2025-05-23 12:00:00.000000', 'Alice Smith', b'1', b'0', b'1', 'Engineer', '$2a$10$abc456def789', '+255766789012', 'https://images.unsplash.com/photo-1580489944761-15a19d654956', '15', b'1', 'asmith'),
(10, '505 Math St, Dodoma', b'1', 'Mathematics education specialist', '1984-02-10', 'Mathematics', 'PhD Mathematics', 'robert.johnson@stemapp.com', '2025-05-23 11:00:00.000000', 'Robert Johnson', b'0', b'1', b'0', 'Mathematician', '$2a$10$mno123pqr789', '+255777890123', 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d', '20', b'0', 'rjohnson');

-- --------------------------------------------------------

--
-- Dumping data for table `user_roles`
--

INSERT INTO `user_roles` (`user_id`, `role_id`) VALUES
(2, (SELECT Id FROM roles WHERE name = 'ROLE_ADMIN')),
(2, (SELECT Id FROM roles WHERE name = 'ROLE_USER')),
(3, (SELECT Id FROM roles WHERE name = 'ROLE_USER')),
(4, (SELECT Id FROM roles WHERE name = 'ROLE_USER')),
(5, (SELECT Id FROM roles WHERE name = 'ROLE_USER')),
(6, (SELECT Id FROM roles WHERE name = 'ROLE_USER')),
(7, (SELECT Id FROM roles WHERE name = 'ROLE_USER')),
(8, (SELECT Id FROM roles WHERE name = 'ROLE_USER')),
(9, (SELECT Id FROM roles WHERE name = 'ROLE_USER')),
(10, (SELECT Id FROM roles WHERE name = 'ROLE_USER'));

-- --------------------------------------------------------

--
-- Dumping data for table `blog_posts`
--

INSERT INTO `blog_posts` (`id`, `alt`, `content`, `createdAt`, `document_url`, `image`, `comments`, `likes`, `title`, `updatedAt`, `user_id`, `category_id`) VALUES
(1, 'STEM Classroom', 'Tanzania''s future depends on developing strong STEM capabilities in its youth. This article explores the current challenges and opportunities in building robust STEM education programs across secondary schools in Tanzania.', '2025-05-20 08:00:00.000000', 'documents/stem-capacity-tz.pdf', 'https://afrikahayat.org/wp-content/uploads/2023/12/cambodia-student-reads-class.jpg', 8, 24, 'Building STEM Capacity in Tanzania''s Secondary Schools', '2025-05-20 08:00:00.000000', 3, 'stem'),
(2, 'School Laboratory', 'Practical laboratory experience is crucial for science education, but many Tanzanian schools face resource constraints. Learn how schools are creating innovative laboratory solutions with minimal resources.', '2025-05-19 09:30:00.000000', 'documents/school-laboratories.pdf', 'https://images.squarespace-cdn.com/content/v1/6037c51efd013540f2cb8756/1648654975736-FOIRDAT938BO8EKLTD3R/Milembe+girls+at+lab+slider.jpg?format=1000w', 5, 18, 'Designing Effective School Laboratories with Limited Resources', '2025-05-19 09:30:00.000000', 4, 'laboratories'),
(3, 'Teacher Training', 'Well-trained teachers are the backbone of effective science education. This article discusses modern approaches to professional development for science teachers in Tanzania''s secondary schools.', '2025-05-18 10:15:00.000000', 'documents/teacher-training.pdf', 'https://staticsb.we.org/f/52095/1152x640/c29fe47cc4/tanzania-carousel-9.jpg', 12, 32, 'Empowering Science Teachers: Professional Development Approaches', '2025-05-18 10:15:00.000000', 5, 'teacher'),
(4, 'Community Education', 'Community involvement is essential for sustainable education improvement. Discover effective strategies for engaging parents and community members in supporting STEM education initiatives in Tanzania.', '2025-05-17 11:00:00.000000', 'documents/community-engagement.pdf', 'https://lsi.fsu.edu/sites/g/files/upcbnu1926/files/2024-04/IMGP2617-2.jpg', 9, 27, 'Engaging Parents and Communities in STEM Education', '2025-05-17 11:00:00.000000', 6, 'community'),
(5, 'Women in Science', 'Gender disparities persist in STEM fields across Tanzania. This article explores innovative approaches to encourage more female students to pursue science and mathematics education.', '2025-05-16 13:45:00.000000', 'documents/women-in-stem.pdf', 'https://africa.unwomen.org/sites/default/files/2023-05/IMG_0329%20%281%29_1.JPG', 16, 41, 'Breaking Barriers: Increasing Female Participation in STEM Fields', '2025-05-16 13:45:00.000000', 7, 'stem'),
(6, 'Quantum Computing', 'Exploring the fundamentals of quantum computing and its potential applications in solving complex problems.', '2025-05-15 09:30:00.000000', 'documents/quantum-computing.pdf', 'https://images.unsplash.com/photo-1635070041078-e363dbe005cb', 14, 56, 'Introduction to Quantum Computing', '2025-05-20 11:45:00.000000', 8, 'technology'),
(7, 'Sustainable Engineering', 'Innovative engineering solutions for sustainable development in African contexts.', '2025-05-14 14:15:00.000000', 'documents/sustainable-engineering.pdf', 'https://images.unsplash.com/photo-1605152276897-4f618f831968', 7, 38, 'Sustainable Engineering Practices', '2025-05-16 16:30:00.000000', 9, 'engineering'),
(8, 'Advanced Mathematics', 'New approaches to teaching complex mathematical concepts in secondary education.', '2025-05-13 10:00:00.000000', 'documents/advanced-math.pdf', 'https://images.unsplash.com/photo-1635070041078-e363dbe005cb', 11, 42, 'Modern Mathematics Education', '2025-05-15 09:15:00.000000', 10, 'mathematics');

-- --------------------------------------------------------

--
-- Dumping data for table `comments`
--

INSERT INTO `comments` (`id`, `approved`, `approvedAt`, `content`, `createdAt`, `guestAuthorEmail`, `guestAuthorName`, `approved_by_user_id`, `blog_post_id`) VALUES
(1, b'1', '2025-05-20 09:00:00.000000', 'Great insights on STEM education!', '2025-05-20 08:30:00.000000', NULL, NULL, 2, 1),
(2, b'1', '2025-05-19 10:30:00.000000', 'Innovative lab solutions are inspiring!', '2025-05-19 10:00:00.000000', 'guest@example.com', 'Guest User', 2, 2),
(3, b'0', NULL, 'More teacher training resources needed.', '2025-05-18 11:00:00.000000', NULL, NULL, NULL, 3),
(4, b'1', '2025-05-17 12:00:00.000000', 'Community engagement is key!', '2025-05-17 11:30:00.000000', NULL, NULL, 2, 4),
(5, b'1', '2025-05-16 14:30:00.000000', 'Empowering women in STEM is crucial.', '2025-05-16 14:00:00.000000', NULL, NULL, 2, 5),
(6, b'1', '2025-05-15 10:30:00.000000', 'Quantum computing explained well!', '2025-05-15 10:00:00.000000', NULL, NULL, 2, 6),
(7, b'0', NULL, 'Sustainable engineering is the future.', '2025-05-14 15:00:00.000000', NULL, NULL, NULL, 7),
(8, b'1', '2025-05-13 11:00:00.000000', 'Math education needs innovation.', '2025-05-13 10:30:00.000000', NULL, NULL, 2, 8);

-- --------------------------------------------------------

--
-- Dumping data for table `subscriptions`
--

INSERT INTO `subscriptions` (`id`, `email`, `is_active`, `subscribed_at`) VALUES
(1, 'john@gmail.com', b'1', '2025-05-20 13:03:35.000000'),
(2, 'haroon@gmail.com', b'1', '2025-05-20 13:16:39.000000'),
(3, 'alice@example.com', b'1', '2025-05-21 10:15:22.000000'),
(4, 'bob@example.com', b'1', '2025-05-21 10:16:45.000000'),
(5, 'carol@example.com', b'1', '2025-05-21 10:18:03.000000'),
(6, 'dave@example.com', b'1', '2025-05-22 10:19:30.000000'),
(7, 'eve@example.com', b'1', '2025-05-22 10:20:15.000000'),
(8, 'frank@example.com', b'0', '2025-05-20 14:22:40.000000'),
(9, 'grace@example.com', b'1', '2025-05-23 10:25:55.000000'),
(10, 'henry@example.com', b'1', '2025-05-23 10:27:10.000000'),
(11, 'irene@example.com', b'0', '2025-05-19 09:30:45.000000'),
(12, 'jack@example.com', b'1', '2025-05-23 10:30:20.000000');

-- --------------------------------------------------------

--
-- Dumping data for table `refresh_tokens`
--

INSERT INTO `refresh_tokens` (`id`, `expiryDate`, `token`, `user_id`) VALUES
(1, '2025-06-23 20:00:00.000000', 'abc123-refresh-token', 3),
(2, '2025-06-23 20:00:00.000000', 'xyz789-refresh-token', 4),
(3, '2025-06-23 20:00:00.000000', 'def456-refresh-token', 5),
(4, '2025-06-23 20:00:00.000000', 'ghi789-refresh-token', 6),
(5, '2025-06-23 20:00:00.000000', 'jkl123-refresh-token', 7),
(6, '2025-06-23 20:00:00.000000', 'mno456-refresh-token', 8),
(7, '2025-06-23 20:00:00.000000', 'pqr789-refresh-token', 9),
(8, '2025-06-23 20:00:00.000000', 'stu123-refresh-token', 10);

COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;


--
--DELIMITER $$
--
--CREATE TRIGGER after_comment_insert
--AFTER INSERT ON comments
--FOR EACH ROW
--BEGIN
--  IF NEW.approved = 1 THEN
--    UPDATE blog_posts
--    SET comments = (
--      SELECT COUNT(*)
--      FROM comments
--      WHERE blog_post_id = NEW.blog_post_id
--        AND approved = 1
--    )
--    WHERE id = NEW.blog_post_id;
--  END IF;
--END$$
--
--DELIMITER ;
--
--
--
--
--DELIMITER $$
--
--CREATE TRIGGER after_comment_update
--AFTER UPDATE ON comments
--FOR EACH ROW
--BEGIN
--  IF OLD.approved != NEW.approved THEN
--    UPDATE blog_posts
--    SET comments = (
--      SELECT COUNT(*)
--      FROM comments
--      WHERE blog_post_id = NEW.blog_post_id
--        AND approved = 1
--    )
--    WHERE id = NEW.blog_post_id;
--  END IF;
--END$$
--
--DELIMITER ;
