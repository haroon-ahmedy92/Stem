-- Team Members Table Creation Script
-- This script creates the team_members table as per the API specification

CREATE TABLE IF NOT EXISTS team_members (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    qualification VARCHAR(500) NOT NULL,
    role VARCHAR(255) NOT NULL,
    address VARCHAR(500),
    email VARCHAR(255),
    phone VARCHAR(20),
    profile_image VARCHAR(500),
    bio TEXT,
    linkedin VARCHAR(500),
    research_interests JSON,
    publications JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Indexes for better performance
    INDEX idx_team_members_name (name),
    INDEX idx_team_members_role (role),
    INDEX idx_team_members_email (email),
    INDEX idx_team_members_created_at (created_at)
);

-- Insert sample data (matching the API specification examples)
INSERT INTO team_members (name, qualification, role, address, email, phone, bio, research_interests, publications) VALUES 
(
    'Prof. Julius Nyahongo',
    'Socio-ecology research',
    'Principal Investigator (PI)',
    'P. O Box 523, Dodoma',
    'nyahongo.jw@gmail.com',
    NULL,
    'Professor Julius Nyahongo is a renowned expert in socio-ecology research with extensive experience in environmental sustainability and community development.',
    JSON_ARRAY('Socio-ecology', 'Environmental sustainability', 'Community development'),
    JSON_ARRAY(
        JSON_OBJECT('title', 'Socio-ecological systems in Tanzania', 'year', 2023, 'journal', 'Journal of Environmental Studies')
    )
),
(
    'Dr. Rose E. Matete',
    'Dr. of Philosophy in Educational Management (PhD)',
    'Co PI',
    'P.O. Box 523, Dodoma',
    'roseem2010@gmail.com',
    '+255 656 829 781',
    'Dr. Rose E. Matete is an expert in educational management with a focus on improving educational systems and leadership development.',
    JSON_ARRAY('Educational Management', 'Leadership Development', 'Educational Policy'),
    JSON_ARRAY(
        JSON_OBJECT('title', 'Educational Leadership in Developing Countries', 'year', 2022, 'journal', 'Education Management Review')
    )
),
(
    'Dr. Michael Johnson',
    'PhD in Computer Science',
    'Research Associate',
    'P.O. Box 789, Dodoma',
    'michael.johnson@email.com',
    '+255 123 456 789',
    'Dr. Michael Johnson specializes in computer science research with a focus on artificial intelligence and machine learning applications.',
    JSON_ARRAY('Artificial Intelligence', 'Machine Learning', 'Data Science'),
    JSON_ARRAY(
        JSON_OBJECT('title', 'AI Applications in Education', 'year', 2023, 'journal', 'AI Research Quarterly')
    )
),
(
    'Prof. Sarah Williams',
    'Professor of Environmental Science',
    'Senior Researcher',
    'P.O. Box 456, Dodoma',
    'sarah.williams@email.com',
    '+255 987 654 321',
    'Professor Sarah Williams is a leading environmental scientist working on climate change adaptation and sustainable development projects.',
    JSON_ARRAY('Climate Change', 'Sustainable Development', 'Environmental Policy'),
    JSON_ARRAY(
        JSON_OBJECT('title', 'Climate Adaptation Strategies', 'year', 2023, 'journal', 'Environmental Science Journal'),
        JSON_OBJECT('title', 'Sustainable Development in East Africa', 'year', 2022, 'journal', 'Development Studies Review')
    )
);
