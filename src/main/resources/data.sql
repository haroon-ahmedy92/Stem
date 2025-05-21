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








-- Inserting data into admins table (only SUPER_ADMIN and ADMIN)
INSERT INTO `admins` (`id`, `accountNonExpired`, `accountNonLocked`, `credentialsNonExpired`, `email`, `enabled`, `password`, `username`) VALUES
(1, b'1', b'1', b'1', 'superadmin@stemapp.com', b'1', '$2a$10$tByEQwKElelaryoIfXv20./BQa7YLEFycaUVVmbVeLbN/vjqpSB1S', 'superadmin'),
(2, b'1', b'1', b'1', 'admin@stemapp.com', b'1', '$2a$10$XptfskLsT1l/bRTLRiiCgejHqOpgXFreUnNUa35gJdCr2v2QbVFzu', 'admin');

-- Inserting data into admin_roles table
INSERT INTO `admin_roles` (`admin_id`, `roles`) VALUES
(1, 'ROLE_SUPER_ADMIN'),
(2, 'ROLE_ADMIN');

-- Inserting data into categories table (including 'All' category)
INSERT INTO `categories` (`id`, `name`) VALUES
('all', 'All'),
('stem', 'STEM Education'),
('laboratories', 'Laboratories'),
('teacher', 'Teacher Training'),
('community', 'Community Engagement'),
('science', 'Science'),
('technology', 'Technology'),
('engineering', 'Engineering'),
('mathematics', 'Mathematics');

-- Inserting data into authors table with profile pictures
INSERT INTO `authors` (`id`, `image`, `initials`, `name`) VALUES
(1, 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2', 'RM', 'Dr. Rose E. Matete'),
(2, 'https://images.unsplash.com/photo-1560250097-0b93528c311a', 'OW', 'Dr. Ombeni W. Msuya'),
(3, 'https://images.unsplash.com/photo-1542190891-2093d38760f2', 'AJ', 'Dr. Abdallah J. Seni'),
(4, 'https://images.unsplash.com/photo-1544005313-94ddf0286df2', 'CE', 'Christopher Emily'),
(5, 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d', 'AK', 'Andrew Keha Kuko'),
(6, 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2', 'JD', 'John Doe'),
(7, 'https://images.unsplash.com/photo-1580489944761-15a19d654956', 'AS', 'Alice Smith'),
(8, 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d', 'RJ', 'Robert Johnson');

-- Inserting data into blog_posts table with provided image URLs
INSERT INTO `blog_posts` (`id`, `alt`, `content`, `createdAt`, `document_url`, `image`, `comments`, `likes`, `title`, `updatedAt`, `author_id`, `category_id`) VALUES
(1, 'STEM Classroom', 'Tanzania''s future depends on developing strong STEM capabilities in its youth. This article explores the current challenges and opportunities in building robust STEM education programs across secondary schools in Tanzania.', '2022-11-10 08:00:00.000000', 'documents/stem-capacity-tz.pdf', 'https://afrikahayat.org/wp-content/uploads/2023/12/cambodia-student-reads-class.jpg', 8, 24, 'Building STEM Capacity in Tanzania''s Secondary Schools', '2022-11-10 08:00:00.000000', 1, 'stem'),
(2, 'School Laboratory', 'Practical laboratory experience is crucial for science education, but many Tanzanian schools face resource constraints. Learn how schools are creating innovative laboratory solutions with minimal resources.', '2022-11-05 09:30:00.000000', 'documents/school-laboratories.pdf', 'https://images.squarespace-cdn.com/content/v1/6037c51efd013540f2cb8756/1648654975736-FOIRDAT938BO8EKLTD3R/Milembe+girls+at+lab+slider.jpg?format=1000w', 5, 18, 'Designing Effective School Laboratories with Limited Resources', '2022-11-05 09:30:00.000000', 2, 'laboratories'),
(3, 'Teacher Training', 'Well-trained teachers are the backbone of effective science education. This article discusses modern approaches to professional development for science teachers in Tanzania''s secondary schools.', '2022-10-28 10:15:00.000000', 'documents/teacher-training.pdf', 'https://staticsb.we.org/f/52095/1152x640/c29fe47cc4/tanzania-carousel-9.jpg', 12, 32, 'Empowering Science Teachers: Professional Development Approaches', '2022-10-28 10:15:00.000000', 3, 'teacher'),
(4, 'Community Education', 'Community involvement is essential for sustainable education improvement. Discover effective strategies for engaging parents and community members in supporting STEM education initiatives in Tanzania.', '2022-10-21 11:00:00.000000', 'documents/community-engagement.pdf', 'https://lsi.fsu.edu/sites/g/files/upcbnu1926/files/2024-04/IMGP2617-2.jpg', 9, 27, 'Engaging Parents and Communities in STEM Education', '2022-10-21 11:00:00.000000', 4, 'community'),
(5, 'Women in Science', 'Gender disparities persist in STEM fields across Tanzania. This article explores innovative approaches to encourage more female students to pursue science and mathematics education.', '2022-10-14 13:45:00.000000', 'documents/women-in-stem.pdf', 'https://africa.unwomen.org/sites/default/files/2023-05/IMG_0329%20%281%29_1.JPG', 16, 41, 'Breaking Barriers: Increasing Female Participation in STEM Fields', '2022-10-14 13:45:00.000000', 5, 'stem'),
(6, 'Quantum Computing', 'Exploring the fundamentals of quantum computing and its potential applications in solving complex problems.', '2023-01-15 09:30:00.000000', 'documents/quantum-computing.pdf', 'https://images.unsplash.com/photo-1635070041078-e363dbe005cb', 14, 56, 'Introduction to Quantum Computing', '2023-01-20 11:45:00.000000', 6, 'technology'),
(7, 'Sustainable Engineering', 'Innovative engineering solutions for sustainable development in African contexts.', '2023-02-10 14:15:00.000000', 'documents/sustainable-engineering.pdf', 'https://images.unsplash.com/photo-1605152276897-4f618f831968', 7, 38, 'Sustainable Engineering Practices', '2023-02-12 16:30:00.000000', 7, 'engineering'),
(8, 'Advanced Mathematics', 'New approaches to teaching complex mathematical concepts in secondary education.', '2023-03-05 10:00:00.000000', 'documents/advanced-math.pdf', 'https://images.unsplash.com/photo-1635070041078-e363dbe005cb', 11, 42, 'Modern Mathematics Education', '2023-03-08 09:15:00.000000', 8, 'mathematics');

-- Inserting data into subscriptions table
INSERT INTO `subscriptions` (`id`, `email`, `is_active`, `subscribed_at`) VALUES
(1, 'john@gmail.com', b'1', '2025-04-25 13:03:35.000000'),
(2, 'haroon@gmail.com', b'1', '2025-04-25 13:16:39.000000'),
(3, 'alice@example.com', b'1', '2025-04-26 10:15:22.000000'),
(4, 'bob@example.com', b'1', '2025-04-26 10:16:45.000000'),
(5, 'carol@example.com', b'1', '2025-04-26 10:18:03.000000'),
(6, 'dave@example.com', b'1', '2025-04-26 10:19:30.000000'),
(7, 'eve@example.com', b'1', '2025-04-26 10:20:15.000000'),
(8, 'frank@example.com', b'0', '2025-04-25 14:22:40.000000'),
(9, 'grace@example.com', b'1', '2025-04-26 10:25:55.000000'),
(10, 'henry@example.com', b'1', '2025-04-26 10:27:10.000000'),
(11, 'irene@example.com', b'0', '2025-04-24 09:30:45.000000'),
(12, 'jack@example.com', b'1', '2025-04-26 10:30:20.000000');