package com.stemapplication; // Or your main package

import com.stemapplication.Models.*;
import com.stemapplication.Repository.*;
import com.stemapplication.Service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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

    public DataInitializer(AuthService authService,
                          AboutBackgroundRepository backgroundRepository,
                          AboutBackgroundSectionRepository backgroundSectionRepository,
                          StemBenefitRepository benefitRepository,
                          AboutJustificationRepository justificationRepository,
                          JustificationReferenceRepository referenceRepository,
                          AboutObjectivesRepository objectivesRepository,
                          SpecificObjectiveRepository specificObjectiveRepository) {
        this.authService = authService;
        this.backgroundRepository = backgroundRepository;
        this.backgroundSectionRepository = backgroundSectionRepository;
        this.benefitRepository = benefitRepository;
        this.justificationRepository = justificationRepository;
        this.referenceRepository = referenceRepository;
        this.objectivesRepository = objectivesRepository;
        this.specificObjectiveRepository = specificObjectiveRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        authService.createSuperAdminIfNotExists();
        initializeAboutPageData();
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
}