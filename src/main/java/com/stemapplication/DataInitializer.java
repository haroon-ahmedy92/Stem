package com.stemapplication;

import com.stemapplication.Models.*;
import com.stemapplication.Repository.*;
import com.stemapplication.Service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AuthService authService;
    private final AboutBackgroundRepository backgroundRepository;
    private final AboutBackgroundSectionRepository backgroundSectionRepository;
    private final StemBenefitRepository benefitRepository;
    private final AboutJustificationRepository justificationRepository;
    private final JustificationReferenceRepository referenceRepository;
    private final AboutObjectivesRepository objectivesRepository;
    private final SpecificObjectiveRepository specificObjectiveRepository;
    
    // Homepage repositories
    private final HomepageHeroRepository homepageHeroRepository;
    private final HomepageActivityRepository homepageActivityRepository;
    private final HomepageOutcomeRepository homepageOutcomeRepository;
    private final HomepageSectionRepository homepageSectionRepository;

    public DataInitializer(AuthService authService,
                          AboutBackgroundRepository backgroundRepository,
                          AboutBackgroundSectionRepository backgroundSectionRepository,
                          StemBenefitRepository benefitRepository,
                          AboutJustificationRepository justificationRepository,
                          JustificationReferenceRepository referenceRepository,
                          AboutObjectivesRepository objectivesRepository,
                          SpecificObjectiveRepository specificObjectiveRepository,
                          HomepageHeroRepository homepageHeroRepository,
                          HomepageActivityRepository homepageActivityRepository,
                          HomepageOutcomeRepository homepageOutcomeRepository,
                          HomepageSectionRepository homepageSectionRepository) {
        this.authService = authService;
        this.backgroundRepository = backgroundRepository;
        this.backgroundSectionRepository = backgroundSectionRepository;
        this.benefitRepository = benefitRepository;
        this.justificationRepository = justificationRepository;
        this.referenceRepository = referenceRepository;
        this.objectivesRepository = objectivesRepository;
        this.specificObjectiveRepository = specificObjectiveRepository;
        this.homepageHeroRepository = homepageHeroRepository;
        this.homepageActivityRepository = homepageActivityRepository;
        this.homepageOutcomeRepository = homepageOutcomeRepository;
        this.homepageSectionRepository = homepageSectionRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        authService.createSuperAdminIfNotExists();
        initializeAboutPageData();
        initializeHomepageData();
    }
    
    private void initializeAboutPageData() {
        // Initialize About Background
        if (backgroundRepository.count() == 0) {
            initializeBackground();
        }
        
        // Initialize STEM Benefits
        if (benefitRepository.count() == 0) {
            initializeBenefits();
        }
        
        // Initialize Justification
        if (justificationRepository.count() == 0) {
            initializeJustification();
        }
        
        // Initialize Objectives
        if (objectivesRepository.count() == 0) {
            initializeObjectives();
        }
    }
    
    private void initializeBackground() {
        AboutBackground background = new AboutBackground();
        background.setTitle("Background Information");
        background.setMainContent("The science education field has been acknowledged to play a significant role in sustainable development all over the world. In the 21st century, science education is vital for countries' economic competitiveness, peace and security, and general quality of life.");
        background.setCtaText("Learn More About Our Mission");
        background.setCtaLink("/contact");
        
        AboutBackground savedBackground = backgroundRepository.save(background);
        
        // Add background sections
        AboutBackgroundSection section1 = new AboutBackgroundSection();
        section1.setTitle("The Importance of Science Education");
        section1.setContent("Integration of science activities cultivates students' critical thinking skills for them to be able to analyze, evaluate, and make arguments and conclusions correctly and logically about the problems and how they can solve them. Science education is thought to improve livelihood and an important tool for the advancement of socio-economic development in almost all countries. Indeed, Science, Mathematics, and Technology (SMT) is thought to accelerate socio-economic development.");
        section1.setDisplayOrder(1);
        section1.setAboutBackground(savedBackground);
        
        backgroundSectionRepository.save(section1);
    }
    
    private void initializeBenefits() {
        String[] benefitTexts = {
            "Creates active, creative, critical, and communicative individuals",
            "Contributes to scientific and technological innovations and advancement",
            "Enhances fight against diseases, food production, and environmental protection",
            "Drives industrial development and innovations",
            "Promotes tolerance, democracy, political awareness, and respect for dignity",
            "Increases employment opportunities in the fastest-growing job categories"
        };
        
        for (int i = 0; i < benefitTexts.length; i++) {
            StemBenefit benefit = new StemBenefit();
            benefit.setTitle("STEM Benefit " + (i + 1));
            benefit.setDescription(benefitTexts[i]);
            benefit.setIcon("fas fa-check-circle");
            benefit.setDisplayOrder(i + 1);
            benefit.setIsActive(true);
            
            benefitRepository.save(benefit);
        }
    }
    
    private void initializeJustification() {
        AboutJustification justification = new AboutJustification();
        justification.setTitle("Justification of the Project");
        justification.setContent("The STEM-related workforce has been increasingly becoming important in the 21st century and many societies tend to integrate STEM education into the education curriculum with the intention of bringing about meaningful learning among the students. According to Smith (2019), the fastest-growing job categories are related to STEM, and about 90 percent of future jobs will require people with specialization in information and communication technology (ICT) skills.");
        justification.setConclusion("However, it has been observed that many students tend not to join STEM-related subjects and courses in both secondary schools and universities. A recent survey of 2017 in the Dodoma Region in Tanzania indicated a serious problem of lack of science laboratories and a shortage of teachers for science subjects in secondary schools.");
        
        AboutJustification savedJustification = justificationRepository.save(justification);
        
        // Add references
        JustificationReference ref1 = new JustificationReference();
        ref1.setTitle("STEM Education and Future Job Markets");
        ref1.setUrl("https://example.com/stem-education-research");
        ref1.setAuthor("Smith");
        ref1.setPublicationDate("2019");
        ref1.setDisplayOrder(1);
        ref1.setAboutJustification(savedJustification);
        
        JustificationReference ref2 = new JustificationReference();
        ref2.setTitle("Science Education Challenges in Tanzania");
        ref2.setUrl("https://example.com/tanzania-science-education");
        ref2.setAuthor("Matete");
        ref2.setPublicationDate("2022");
        ref2.setDisplayOrder(2);
        ref2.setAboutJustification(savedJustification);
        
        referenceRepository.save(ref1);
        referenceRepository.save(ref2);
    }
    
    private void initializeObjectives() {
        AboutObjectives objectives = new AboutObjectives();
        objectives.setTitle("Project Objectives");
        objectives.setIntroduction("This project intends to strengthen the science related subjects in secondary schools by building capacity to science teachers on STEM related subjects to improve the quality of education that will enable the Tanzanian nation to have graduates who can survive in a competitive economy and labor market place of the 21st century.");
        objectives.setConclusion("Through these comprehensive objectives, we aim to create a sustainable impact on science education in Tanzania.");
        
        AboutObjectives savedObjectives = objectivesRepository.save(objectives);
        
        // Add specific objectives
        String[][] specificObjectives = {
            {"Teacher Training", "Training science teachers in secondary schools to enhance their capacity and teaching methodologies."},
            {"Decision-Maker Involvement", "Training decision-makers to recognize the necessity of emphasizing science subjects in secondary education."},
            {"Community Engagement", "Training local community members and parents to participate actively in the education of their children."},
            {"Laboratory Enhancement", "Strengthening laboratory services for effective teaching and learning of science subjects."}
        };
        
        for (int i = 0; i < specificObjectives.length; i++) {
            SpecificObjective objective = new SpecificObjective();
            objective.setTitle(specificObjectives[i][0]);
            objective.setDescription(specificObjectives[i][1]);
            objective.setDisplayOrder(i + 1);
            objective.setIsActive(true);
            objective.setAboutObjectives(savedObjectives);
            
            specificObjectiveRepository.save(objective);
        }
    }
    
    private void initializeHomepageData() {
        // Initialize Homepage Hero
        if (homepageHeroRepository.count() == 0) {
            initializeHomepageHero();
        }
        
        // Initialize Homepage Activities  
        if (homepageActivityRepository.count() == 0) {
            initializeHomepageActivities();
        }
        
        // Initialize Homepage Outcomes
        if (homepageOutcomeRepository.count() == 0) {
            initializeHomepageOutcomes();
        }
        
        // Initialize Homepage Sections
        if (homepageSectionRepository.count() == 0) {
            initializeHomepageSections();
        }
    }
    
    private void initializeHomepageHero() {
        HomepageHero hero = new HomepageHero();
        hero.setTitle("STEM Education for Tomorrow's Leaders");
        hero.setSubtitle("Empowering students through innovative Science, Technology, Engineering, and Mathematics education");
        hero.setBackgroundImage("/images/stem-hero-bg.jpg");
        hero.setBackgroundGradient("linear-gradient(135deg, #667eea 0%, #764ba2 100%)");
        hero.setCtaText("Explore Our Mission");
        hero.setCtaLink("/about");
        hero.setCtaColor("#1976d2");
        hero.setSearchEnabled(true);
        hero.setSearchPlaceholder("Search STEM programs...");
        hero.setIsPublished(true);
        
        homepageHeroRepository.save(hero);
    }
    
    private void initializeHomepageActivities() {
        String[][] activitiesData = {
            {"Teacher Training Programs", 
                "Comprehensive training for science teachers to enhance their teaching methodologies and subject expertise",
                "fas fa-chalkboard-teacher", "#1976d2"},
            {"Laboratory Enhancement", 
                "Upgrading and strengthening laboratory facilities for effective hands-on science learning",
                "fas fa-flask", "#388e3c"},
            {"Community Engagement", 
                "Involving local communities and parents in supporting STEM education initiatives",
                "fas fa-users", "#f57c00"},
            {"Student Mentorship", 
                "One-on-one mentoring programs to guide students in STEM career paths",
                "fas fa-user-graduate", "#7b1fa2"},
            {"Educational Resources", 
                "Developing and distributing quality educational materials for science subjects",
                "fas fa-book", "#c2185b"},
            {"Research & Innovation", 
                "Promoting research culture and innovation among students and teachers",
                "fas fa-lightbulb", "#00796b"}
        };
        
        for (int i = 0; i < activitiesData.length; i++) {
            HomepageActivity activity = new HomepageActivity();
            activity.setTitle(activitiesData[i][0]);
            activity.setDescription(activitiesData[i][1]);
            activity.setIconClass(activitiesData[i][2]);
            activity.setColor(activitiesData[i][3]);
            activity.setOrderIndex(i + 1);
            activity.setIsFeatured(i < 3); // First 3 are featured
            activity.setIsPublished(true);
            
            homepageActivityRepository.save(activity);
        }
    }
    
    private void initializeHomepageOutcomes() {
        String[][] outcomesData = {
            {"Teachers Trained", "500+", 
                "Secondary school science teachers have completed our comprehensive training programs",
                "fas fa-chalkboard-teacher"},
            {"Schools Impacted", "120+", 
                "Secondary schools across Tanzania have benefited from our STEM initiatives",
                "fas fa-school"},
            {"Students Reached", "15,000+", 
                "Students have experienced improved STEM education through our programs",
                "fas fa-user-graduate"},
            {"Labs Enhanced", "80+", 
                "Science laboratories have been upgraded with modern equipment and resources",
                "fas fa-flask"},
            {"Communities Engaged", "50+", 
                "Local communities are actively participating in supporting STEM education",
                "fas fa-users"},
            {"Success Rate", "95%", 
                "Of participating students show improved performance in science subjects",
                "fas fa-chart-line"}
        };
        
        for (int i = 0; i < outcomesData.length; i++) {
            HomepageOutcome outcome = new HomepageOutcome();
            outcome.setTitle(outcomesData[i][0]);
            outcome.setDescription(outcomesData[i][2]);
            outcome.setIconClass(outcomesData[i][3]);
            outcome.setOrderIndex(i + 1);
            outcome.setIsPublished(true);
            outcome.setStatus(HomepageOutcome.OutcomeStatus.ON_TRACK);
            
            // Add metrics as simple map (will be stored as TEXT)
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("value", outcomesData[i][1]);
            outcome.setMetrics(metrics);
            
            homepageOutcomeRepository.save(outcome);
        }
    }
    
    private void initializeHomepageSections() {
        // Activities Section
        HomepageSection activitiesSection = new HomepageSection();
        activitiesSection.setSectionType(HomepageSection.SectionType.ACTIVITIES);
        activitiesSection.setTitle("Our Key Activities");
        activitiesSection.setSubtitle("Transforming STEM Education in Tanzania");
        activitiesSection.setDescription("We focus on comprehensive programs that address the core challenges in science education, from teacher training to community engagement.");
        activitiesSection.setBackgroundColor("#f8f9fa");
        activitiesSection.setContentBackground("#ffffff");
        activitiesSection.setIsPublished(true);
        
        // Outcomes Section
        HomepageSection outcomesSection = new HomepageSection();
        outcomesSection.setSectionType(HomepageSection.SectionType.OUTCOMES);
        outcomesSection.setTitle("Our Impact & Achievements");
        outcomesSection.setSubtitle("Measurable Results in STEM Education");
        outcomesSection.setDescription("See the tangible impact we've made in improving science education across Tanzania through our various programs and initiatives.");
        outcomesSection.setBackgroundColor("#e3f2fd");
        outcomesSection.setContentBackground("#ffffff");
        outcomesSection.setIsPublished(true);
        
        // Monitoring Section
        HomepageSection monitoringSection = new HomepageSection();
        monitoringSection.setSectionType(HomepageSection.SectionType.MONITORING);
        monitoringSection.setTitle("Monitoring & Evaluation");
        monitoringSection.setSubtitle("Ensuring Quality and Effectiveness");
        monitoringSection.setDescription("We continuously monitor and evaluate our programs to ensure they meet the highest standards and deliver meaningful impact to students and teachers.");
        monitoringSection.setBackgroundColor("#f3e5f5");
        monitoringSection.setContentBackground("#ffffff");
        monitoringSection.setIsPublished(true);
        
        // Ethics Section
        HomepageSection ethicsSection = new HomepageSection();
        ethicsSection.setSectionType(HomepageSection.SectionType.ETHICS);
        ethicsSection.setTitle("Ethics & Integrity");
        ethicsSection.setSubtitle("Committed to Ethical Excellence");
        ethicsSection.setDescription("Our commitment to ethical practices ensures that all our STEM education initiatives are conducted with the highest levels of integrity and transparency.");
        ethicsSection.setBackgroundColor("#e8f5e8");
        ethicsSection.setContentBackground("#ffffff");
        ethicsSection.setIsPublished(true);
        
        homepageSectionRepository.save(activitiesSection);
        homepageSectionRepository.save(outcomesSection);
        homepageSectionRepository.save(monitoringSection);
        homepageSectionRepository.save(ethicsSection);
    }
}