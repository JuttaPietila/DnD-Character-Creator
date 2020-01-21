/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * This class contains information of a single Dnd character. The class uses objects
 * to managing information of a character; name, race, class, backgound, alignment and 
 * features like age, sex, height, weight, eyes, skin and hair. Free descriptions of 
 * the character's personality, ideals, bonds, flaws and background story are also
 * included. Character class contains also stats about a single character's abilities 
 * and skills. The total ability scores are in int form, all the other information
 * is gathered in String form.
 * 
 * Character class also provides methods for saving and loading a character 
 * information as/from a txt file.
 * 
 * @author Jutta Pietila
 */
public class Character {
    
    public static void main(String[] args) {
        String fileName = args[0];
        System.out.println("Loading a character from file " + fileName);
        Character DnDCharacter = Character.loadCharacter(fileName);
        System.out.println(DnDCharacter);       
    }  
    
    // Headers for the basic information of the character.
    public static String CHARACTER_START_LINE = "CHARACTER";
    public static String NAME_HEADER = "name: ";
    public static String RACE_HEADER = "race: ";
    public static String CLASS_HEADER = "class: ";
    public static String BACKGROUND_HEADER = "background: ";
    public static String ALIGNMENT_HEADER = "alignment: ";
       
    // Headers for character information from the table.
    public static String AGE_HEADER = "age: ";
    public static String SEX_HEADER = "sex: ";
    public static String HEIGHT_HEADER = "height (cm): ";
    public static String WEIGHT_HEADER = "weight (kg): ";
    public static String EYES_HEADER = "eyes: ";
    public static String SKIN_HEADER = "skin: ";
    public static String HAIR_HEADER = "hair: ";
    
    /* Headers and ending indetifiers for character descriptions from text fields.
     * The ending identifiers needs to be used, so that when loading a character
     * from a file, all the lines from the text areas are returned to the UI.
     */
    public static String PERSONALITY_HEADER = "personality traits: ";
    public static String PERSONALITY_END = "end_of_personality";
    public static String IDEALS_HEADER = "ideals: ";
    public static String IDEALS_END = "end_of_ideals";
    public static String BONDS_HEADER = "bonds: ";
    public static String BONDS_END = "end_of_bonds";
    public static String FLAWS_HEADER = "flaws: ";
    public static String FLAWS_END = "end_of_flaws";
    public static String FEATURESTRAITS_HEADER = "features & traits: ";
    public static String FEATURESTRAITS_END = "end_of_feats_traits";
    public static String BACKGROUNDSTORY_HEADER = "additional background story: ";
    public static String BACKGROUND_S_END = "end_of_background_story";
    
    // Boolean that updates, when a text areas lines are ended
    public static boolean endOfText = false;
    
    // Headers for the ability point scoring.
    public static String ABILITIES_START_LINE = "ABILITIES:";
    public static String STR_HEADER = "STR: ";
    public static String DEX_HEADER = "DEX: ";
    public static String CON_HEADER = "CON: ";
    public static String INT_HEADER = "INT: ";
    public static String WIS_HEADER = "WIS: ";
    public static String CHA_HEADER = "CHA: ";
    
    // Headers for the skill points and proficiencies.
    public static String SKILLS_START_LINE = "SKILLS:";
    public static String ACROBATICS_HEADER = "acrobatics (dex): ";
    public static String ANIMALHANDLING_HEADER = "animal handling (wis): ";
    public static String ARCANA_HEADER = "arcana (int): ";
    public static String ATHLETICS_HEADER = "athletics (str): ";
    public static String DECEPTION_HEADER = "deception (cha): ";
    public static String HISTORY_HEADER = "history (int): ";
    public static String INSIGHT_HEADER = "insight (wis): ";
    public static String INTIMIDATION_HEADER = "intimidation (cha): ";
    public static String INVESTIGATION_HEADER = "investigation (int): ";
    public static String MEDICINE_HEADER = "medicine (wis): ";
    public static String NATURE_HEADER = "nature (int): ";
    public static String PERCEPTION_HEADER = "perception (wis): ";
    public static String PERFORMANCE_HEADER = "performance (cha): ";
    public static String PERSUASION_HEADER = "persuasion (cha): ";
    public static String RELIGION_HEADER = "religion (int): ";
    public static String SLEIGHTOFHAND_HEADER = "sleight of hand: ";
    public static String STEALTH_HEADER = "stealth (dex): ";
    public static String SURVIVAL_HEADER = "survival (wis): ";
    public static String PROFICIENCIES_HEADER = "skill proficiencies:";

    String name;
    String race;
    String characterClass;
    String background;
    String alignment;
    
    String age;
    String sex;
    String height;
    String weight;
    String eyes;
    String skin;
    String hair;
    
    String personality;
    String ideals;
    String bonds;
    String flaws;
    String featsAndTraits;
    String backgroundStory;
    
    int strength;
    int dexterity;
    int constitution;
    int intelligence;
    int wisdom;
    int charisma;
    
    String acrobatics;
    String animalHandl;
    String arcana;
    String athletics;
    String deception;
    String history;
    String insight;
    String intimidation;
    String investigation;
    String medicine;
    String nature;
    String perception;
    String performance;
    String persuasion;
    String religion;
    String sleightOfH;
    String stealth;
    String survival;
    
    String proficiencies; 
    
    /**
     * Method that saves the given character to the file with given name.
     * @param c the value of c (Character object)
     * @param file the value of file (file's name)
     */
    public static void saveCharacter(Character c, String file) {
        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(new 
            FileOutputStream(file), "UTF-8"));
            
            writer.write(CHARACTER_START_LINE + "\n");
            writer.write(NAME_HEADER + c.getName() + "\n");
            writer.write(RACE_HEADER + c.getRace() + "\n");
            writer.write(CLASS_HEADER + c.getCharacterClass() + "\n");
            writer.write(BACKGROUND_HEADER + c.getBackground() + "\n");
            writer.write(ALIGNMENT_HEADER + c.getAlignment() + "\n");
                
            writer.write(AGE_HEADER + c.getAge() + "\n");
            writer.write(SEX_HEADER + c.getSex() + "\n");
            writer.write(HEIGHT_HEADER + c.getHeight() + "\n");
            writer.write(WEIGHT_HEADER + c.getWeight() + "\n");
            writer.write(EYES_HEADER + c.getEyes() + "\n");
            writer.write(SKIN_HEADER + c.getSkin() + "\n");
            writer.write(HAIR_HEADER + c.getHair() + "\n");
                
            writer.write(PERSONALITY_HEADER + c.getPersonality() + "\n" +
            PERSONALITY_END + "\n");
            writer.write(IDEALS_HEADER + c.getIdeals() + "\n" +
            IDEALS_END + "\n");
            writer.write(BONDS_HEADER + c.getBonds() + "\n" +
            BONDS_END + "\n");
            writer.write(FLAWS_HEADER + c.getFlaws() + "\n" +
            FLAWS_END + "\n");
            writer.write(FEATURESTRAITS_HEADER + c.getFeatsAndTraits() + "\n" +
            FEATURESTRAITS_END + "\n");
            writer.write(BACKGROUNDSTORY_HEADER + c.getBackgroundStory() + "\n" +
            BACKGROUND_S_END + "\n");
                
            writer.write(STR_HEADER + Integer.toString(c.getStrength()) + "\n");
            writer.write(DEX_HEADER + Integer.toString(c.getDexterity()) + "\n");
            writer.write(CON_HEADER + Integer.toString(c.getConstitution()) + "\n");
            writer.write(INT_HEADER + Integer.toString(c.getIntelligence()) + "\n");
            writer.write(WIS_HEADER + Integer.toString(c.getWisdom()) + "\n");
            writer.write(CHA_HEADER + Integer.toString(c.getCharisma()) + "\n");
                
            writer.write(ACROBATICS_HEADER + c.getAcrobatics() + "\n");
            writer.write(ANIMALHANDLING_HEADER + c.getAnimalHandling() + "\n");
            writer.write(ARCANA_HEADER + c.getArcana() + "\n");
            writer.write(ATHLETICS_HEADER + c.getAthletics() + "\n");
            writer.write(DECEPTION_HEADER + c.getDeception() + "\n");
            writer.write(HISTORY_HEADER + c.getHistory() + "\n");
            writer.write(INSIGHT_HEADER + c.getInsight() + "\n");
            writer.write(INTIMIDATION_HEADER + c.getIntimidation() + "\n");
            writer.write(INVESTIGATION_HEADER + c.getInvestigation() + "\n");
            writer.write(MEDICINE_HEADER + c.getMedicine() + "\n");
            writer.write(NATURE_HEADER + c.getNature() + "\n");
            writer.write(PERCEPTION_HEADER + c.getPerception() + "\n");
            writer.write(PERFORMANCE_HEADER + c.getPerformance() + "\n");
            writer.write(PERSUASION_HEADER + c.getPersuasion() + "\n");
            writer.write(RELIGION_HEADER + c.getReligion() + "\n");
            writer.write(SLEIGHTOFHAND_HEADER + c.getSleightOfHand() + "\n");
            writer.write(STEALTH_HEADER + c.getStealth() + "\n" );
            writer.write(SURVIVAL_HEADER + c.getSurvival() + "\n");
            writer.write(PROFICIENCIES_HEADER + c.getProficiencies());
            
            writer.close();
        } 
        catch (IOException e) {
        }
    }
    
    /**
     * Method that reads the character's features from from a given file and returns 
     * the resulting Character.
     * @param file the value of file
     * @return thefis (file input stream)
     */
    public static Character loadCharacter(String file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            return Character.loadCharacter(fis);
        } catch (IOException e) {
            return null;
        }
    }
    
    /**
     * Method that reads a character's features from InputStream, closes the stream
     * and returns the resulting Character.
     * @param is the value of is (input stream)
     * @return the values of hero (character object)
     */
    public static Character loadCharacter(InputStream is) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            Character hero = new Character();
            String line = reader.readLine();
            String lineFormer = "";
            while (line != null) {
                if (line.startsWith(NAME_HEADER)) {
                    hero.setName(line.substring(NAME_HEADER.length()).trim());
                }
                if (line.startsWith(RACE_HEADER)) {
                    hero.setRace(line.substring(RACE_HEADER.length()).trim());
                }
                if (line.startsWith(CLASS_HEADER)) {
                    hero.setCharacterClass(line.substring(CLASS_HEADER.length()).trim());
                }
                if (line.startsWith(BACKGROUND_HEADER)) {
                    hero.setBackground(line.substring(BACKGROUND_HEADER.length()).trim());
                }
                if (line.startsWith(ALIGNMENT_HEADER)) {
                    hero.setAlignment(line.substring(ALIGNMENT_HEADER.length()).trim());
                }
                if (line.startsWith(AGE_HEADER)) {
                    hero.setAge(line.substring(AGE_HEADER.length()).trim());
                }
                if (line.startsWith(SEX_HEADER)) {
                    hero.setSex(line.substring(SEX_HEADER.length()).trim());
                }
                if (line.startsWith(HEIGHT_HEADER)) {
                    hero.setHeight(line.substring(HEIGHT_HEADER.length()).trim());
                }
                if (line.startsWith(WEIGHT_HEADER)) {
                    hero.setWeight(line.substring(WEIGHT_HEADER.length()).trim());
                }
                if (line.startsWith(EYES_HEADER)) {
                    hero.setEyes(line.substring(EYES_HEADER.length()).trim());
                }
                if (line.startsWith(SKIN_HEADER)) {
                    hero.setSkin(line.substring(SKIN_HEADER.length()).trim());
                }
                if (line.startsWith(HAIR_HEADER)) {
                    hero.setHair(line.substring(HAIR_HEADER.length()).trim());
                }
                /* 
                 * Since there might be several lines in personality, ideals,
                 * bonds, flaws, features & traits and background story, the saved
                 * lines have to be gathered to a String, so that all of them are
                 * returned when loading a character.
                 */
                if (line.startsWith(PERSONALITY_HEADER)) {
                    lineFormer = line.substring(PERSONALITY_HEADER.length()).trim();
                    while (endOfText == false) {
                        line = reader.readLine();
                        if (line.startsWith(PERSONALITY_END)) {
                            endOfText = true;
                        }
                        else {
                            lineFormer = lineFormer + "\n" + line;
                        }   
                    }
                    hero.setPersonality(lineFormer);
                    endOfText = false;
                }
                if (line.startsWith(IDEALS_HEADER)) {
                    lineFormer = line.substring(IDEALS_HEADER.length()).trim();
                    while (endOfText == false) {
                        line = reader.readLine();
                        if (line.startsWith(IDEALS_END)) {
                            endOfText = true;
                        }
                        else {
                            lineFormer = lineFormer + "\n" + line;
                        }  
                    }
                    hero.setIdeals(lineFormer);
                    endOfText = false;
                }
                if (line.startsWith(BONDS_HEADER)) {
                    lineFormer = line.substring(BONDS_HEADER.length()).trim();
                    while (endOfText == false) {
                        line = reader.readLine();
                        if (line.startsWith(BONDS_END)) {
                            endOfText = true;
                        }
                        else {
                            lineFormer = lineFormer + "\n" + line;
                        }  
                    }
                    hero.setBonds(lineFormer);
                    endOfText = false;
                }
                if (line.startsWith(FLAWS_HEADER)) {
                    lineFormer = line.substring(FLAWS_HEADER.length()).trim();
                    while (endOfText == false) {
                        line = reader.readLine();
                        if (line.startsWith(FLAWS_END)) {
                            endOfText = true;
                        }
                        else {
                            lineFormer = lineFormer + "\n" + line; 
                        }  
                    }
                    hero.setFlaws(lineFormer);
                    endOfText = false;
                }
                if (line.startsWith(FEATURESTRAITS_HEADER)) {
                    lineFormer = line.substring(FEATURESTRAITS_HEADER.length()).trim();
                    while (endOfText == false) {
                        line = reader.readLine();
                        if (line.startsWith(FEATURESTRAITS_END)) {
                            endOfText = true;
                        }
                        else {
                            lineFormer = lineFormer + "\n" + line;
                        }  
                    }
                    hero.setFeatsAndTraits(lineFormer);
                    endOfText = false;
                }
                if (line.startsWith(BACKGROUNDSTORY_HEADER)) {
                    lineFormer = line.substring(BACKGROUNDSTORY_HEADER.length()).trim();
                    while (endOfText == false) {
                        line = reader.readLine();
                        if (line.startsWith(BACKGROUND_S_END)) {
                            endOfText = true;
                        }
                        else {
                            lineFormer = lineFormer + "\n" + line;
                        }  
                    }
                    hero.setBackgroundStory(lineFormer);
                    endOfText = false;
                }
                if (line.startsWith(STR_HEADER)) {
                    try {
                        int str = Integer.parseInt(line.substring(STR_HEADER.length()).trim());
                        hero.setStrength(str);
                    } catch (NumberFormatException e) {
                        System.out.println("Error when loading total strength points " + line);
                    }
                }   
                if (line.startsWith(DEX_HEADER)) {
                    try {
                        int dex = Integer.parseInt(line.substring(DEX_HEADER.length()).trim());
                        hero.setDexterity(dex);
                    } catch (NumberFormatException e) {
                        System.out.println("Error when loading total dexterity points " + line);
                    }
                }
                if (line.startsWith(CON_HEADER)) {
                    try {
                        int con = Integer.parseInt(line.substring(CON_HEADER.length()).trim());
                        hero.setConstitution(con);
                    } catch (NumberFormatException e) {
                        System.out.println("Error when loading total constitution points " + line);
                    }
                }
                if (line.startsWith(INT_HEADER)) {
                    try {
                        int inte = Integer.parseInt(line.substring(INT_HEADER.length()).trim());
                        hero.setIntelligence(inte);
                    } catch (NumberFormatException e) {
                        System.out.println("Error when loading total intelligence points " + line);
                    }
                } 
                if (line.startsWith(WIS_HEADER)) {
                    try {
                        int wis = Integer.parseInt(line.substring(WIS_HEADER.length()).trim());
                        hero.setWisdom(wis);
                    } catch (NumberFormatException e) {
                        System.out.println("Error when loading total wisdom points " + line);
                    }
                }
                if (line.startsWith(CHA_HEADER)) {
                    try {
                        int cha = Integer.parseInt(line.substring(CHA_HEADER.length()).trim());
                        hero.setCharisma(cha);
                    } catch (NumberFormatException e) {
                        System.out.println("Error when loading total charisma points " + line);
                    }
                }
                if (line.startsWith(ACROBATICS_HEADER)) {
                    hero.setAcrobatics((line.substring(ACROBATICS_HEADER.length()).trim()));
                }
                if (line.startsWith(ANIMALHANDLING_HEADER)) {
                    hero.setAnimalHandling((line.substring(CHA_HEADER.length()).trim()));
                }
                if (line.startsWith(ARCANA_HEADER)) {
                    hero.setArcana((line.substring(ARCANA_HEADER.length()).trim()));
                }
                if (line.startsWith(ATHLETICS_HEADER)) {
                    hero.setAthletics((line.substring(ATHLETICS_HEADER.length()).trim()));
                }
                if (line.startsWith(DECEPTION_HEADER)) {
                    hero.setDeception((line.substring(DECEPTION_HEADER.length()).trim()));
                }
                if (line.startsWith(HISTORY_HEADER)) {
                    hero.setHistory((line.substring(HISTORY_HEADER.length()).trim()));
                }
                if (line.startsWith(INSIGHT_HEADER)) {
                    hero.setInsight((line.substring(INSIGHT_HEADER.length()).trim()));
                }
                if (line.startsWith(INTIMIDATION_HEADER)) {
                    hero.setIntimidation((line.substring(INTIMIDATION_HEADER.length()).trim()));
                }
                if (line.startsWith(INVESTIGATION_HEADER)) {
                    hero.setInvestigation((line.substring(INVESTIGATION_HEADER.length()).trim()));
                }
                if (line.startsWith(MEDICINE_HEADER)) {
                    hero.setMedicine((line.substring(MEDICINE_HEADER.length()).trim()));
                }
                if (line.startsWith(NATURE_HEADER)) {
                    hero.setNature((line.substring(NATURE_HEADER.length()).trim()));
                }
                if (line.startsWith(PERCEPTION_HEADER)) {
                    hero.setPerception((line.substring(PERCEPTION_HEADER.length()).trim()));
                }
                if (line.startsWith(PERFORMANCE_HEADER)) {
                    hero.setPerformance((line.substring(PERFORMANCE_HEADER.length()).trim()));
                }
                if (line.startsWith(PERSUASION_HEADER)) {
                    hero.setPersuasion((line.substring(PERSUASION_HEADER.length()).trim()));
                }
                if (line.startsWith(RELIGION_HEADER)) {
                    hero.setReligion((line.substring(RELIGION_HEADER.length()).trim()));
                }
                if (line.startsWith(SLEIGHTOFHAND_HEADER)) {
                    hero.setSleightOfHand((line.substring(SLEIGHTOFHAND_HEADER.length()).trim()));
                }
                if (line.startsWith(STEALTH_HEADER)) {
                    hero.setStealth((line.substring(STEALTH_HEADER.length()).trim()));
                }
                if (line.startsWith(SURVIVAL_HEADER)) {
                    hero.setSurvival((line.substring(SURVIVAL_HEADER.length()).trim()));
                }
                if (line.startsWith(PROFICIENCIES_HEADER)) {
                    hero.setProficiencies((line.substring(PROFICIENCIES_HEADER.length()).trim()));
                }

                line = reader.readLine();
            } 

            reader.close();
            return hero;
        } catch (IOException e) {
            return null;
        }
    }
    
    /**
     * Get the value of the name
     *
     * @return the value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Get the value of the race
     *
     * @return the value of race
     */
    public String getRace() {
        return race;
    }
    
    /**
     * Set the value of race
     * 
     * @param race new value of race
     */
    public void setRace(String race) {
        this.race = race;
    }
    
    /**
     * Get the value of the characterClass
     * 
     * @return the value of characterClass
     */
    public String getCharacterClass() {
        return characterClass;
    }
    
    /**
     * Set the value of characterClass
     * 
     * @param characterClass new value characterClass
     */
    public void setCharacterClass(String characterClass) {
        this.characterClass = characterClass;
    }
    
    /**
     * Get the value of the background
     * 
     * @return the value of background
     */
    public String getBackground() {
        return background;
    }
    
    /**
     * Set the value of background
     * 
     * @param background new value background
     */
    public void setBackground(String background) {
        this.background = background;
    }
    
    /**
     * Get the value of the alignment
     * 
     * @return the value of alignment
     */
    public String getAlignment() {
        return alignment;
    }
    
    /**
     * Set the value of alignment
     * 
     * @param alignment new value alignment
     */
    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }
    
    /**
     * Get the value of the age
     * 
     * @return the value of age
     */
    public String getAge() {
        return age;
    }
    
    /**
     * Set the value of age
     * 
     * @param age new value age
     */
    public void setAge(String age) {
        this.age = age;
    }
    
    /**
     * Get the value of the sex
     * 
     * @return the value of sex
     */
    public String getSex() {
        return sex;
    }
    
    /**
     * Set the value of sex
     * 
     * @param sex new value sex
     */
    public void setSex(String sex) {
        this.sex = sex;
    }
    
    /**
     * Get the value of the height
     * 
     * @return the value of height
     */
    public String getHeight() {
        return height;
    }
    
    /**
     * Set the value of height
     * 
     * @param height new value height
     */
    public void setHeight(String height) {
        this.height = height;
    }
    
    /**
     * Get the value of the weight
     * 
     * @return the value of weight
     */
    public String getWeight() {
        return weight;
    }
    
    /**
     * Set the value of weight
     * 
     * @param weight new value weight
     */
    public void setWeight(String weight) {
        this.weight = weight;
    }
    
    /**
     * Get the value of the eyes
     * 
     * @return the value of eyes
     */
    public String getEyes() {
        return eyes;
    }
    
    /**
     * Set the value of eyes
     * 
     * @param eyes new value eyes
     */
    public void setEyes(String eyes) {
        this.eyes = eyes;
    }
    
    /**
     * Get the value of the skin
     * 
     * @return the value of skin
     */
    public String getSkin() {
        return skin;
    }
    
    /**
     * Set the value of skin
     * 
     * @param skin new value skin
     */
    public void setSkin(String skin) {
        this.skin = skin;
    }
    
    /**
     * Get the value of the hair
     * 
     * @return the value of hair
     */
    public String getHair() {
        return hair;
    }
    /**
     * Set the value of hair
     * 
     * @param hair new value of hair
     */
    public void setHair(String hair) {
        this.hair = hair;
    }
    
    /**
     * Get the value of the personality
     * 
     * @return the value of personality
     */
    public String getPersonality() {
        return personality;
    }
    
    /**
     * Set the value of personality
     * 
     * @param personality new value personality
     */
    public void setPersonality(String personality) {
        this.personality = personality;
    }
    
    /**
     * Get the value of the ideals
     * 
     * @return the value of ideals
     */
    public String getIdeals() {
        return ideals;
    }
    
    /**
     * Set the value of ideals
     * 
     * @param ideals new value ideals
     */
    public void setIdeals(String ideals) {
        this.ideals = ideals;
    }
    
    /**
     * Get the value of the bonds
     * 
     * @return the value of bonds
     */
    public String getBonds() {
        return bonds;
    }
    
    /**
     * Set the value of bonds
     * 
     * @param bonds new value bonds
     */
    public void setBonds(String bonds) {
        this.bonds = bonds;
    }
    
    /**
     * Get the value of the flaws
     * 
     * @return the value of flaws
     */
    public String getFlaws() {
        return flaws;
    }
    
    /**
     * Set the value of flaws
     * 
     * @param flaws new value flaws
     */
    public void setFlaws(String flaws) {
        this.flaws = flaws;
    }
    
    /**
     * Get the value of the featsAndTraits
     * 
     * @return the value of featsAndTraits
     */
    public String getFeatsAndTraits() {
        return featsAndTraits;
    }
    
    /**
     * Set the value of featsAndTraits
     * 
     * @param featsAndTraits new value featsAndTraits
     */
    public void setFeatsAndTraits(String featsAndTraits) {
        this.featsAndTraits = featsAndTraits;
    }
    
    /**
     * Get the value of the backgroundStory
     * 
     * @return the value of backgroundStory
     */
    public String getBackgroundStory() {
        return backgroundStory;
    }
    
    /**
     * Set the value of backgroundStory
     * 
     * @param backgroundStory new value backgroundStory
     */
    public void setBackgroundStory(String backgroundStory) {
        this.backgroundStory = backgroundStory;
    }
    
    /**
     * Get the value of the strenth
     * 
     * @return the value of strength
     */
    public int getStrength() {
        return strength;
    }
    
    /**
     * Set the value of strength
     * 
     * @param strength new value strength
     */
    public void setStrength(int strength) {
        this.strength = strength;  
    }
    
    /**
     * Get the value of the dexterity
     * 
     * @return the value of dexterity
     */
    public int getDexterity() {
        return dexterity;
    }
    
    /**
     * Set the value of dexterity
     * 
     * @param dexterity new value dexterity
     */
    public void setDexterity(int dexterity) {
        this.dexterity = dexterity;
    }
    
    /**
     * Get the value of the constitution
     * 
     * @return the value of constitution
     */
    public int getConstitution() {
        return constitution;
    }
    
    /**
     * Set the value of constitution
     * 
     * @param constitution new value constitution
     */
    public void setConstitution(int constitution) {
        this.constitution = constitution;
    }
    
    /**
     * Get the value of the intelligence
     * 
     * @return the value of intelligence
     */
    public int getIntelligence() {
        return intelligence;
    }
    
    /**
     * Set the value of intelligence
     * 
     * @param intelligence new value intelligence
     */
    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }
    
    /**
     * Get the value of the wisdom
     * 
     * @return the value of wisdom
     */
    public int getWisdom() {
        return wisdom;
    }
    
    /**
     * Set the value of wisdom
     * 
     * @param wisdom new value wisdom
     */
    public void setWisdom(int wisdom) {
        this.wisdom = wisdom;
    }
    
    /**
     * Get the value of the charisma
     * 
     * @return the value of charisma
     */
    public int getCharisma() {
        return charisma;
    }
    
    /**
     * Set the value of charisma
     * 
     * @param charisma new value charisma
     */
    public void setCharisma(int charisma) {
        this.charisma = charisma;
    }
    
    /**
     * Get the value of the acrobatics
     * 
     * @return the value of acrobatics
     */
    public String getAcrobatics() {
        return acrobatics;
    }
    
    /**
     * Set the value of acrobatics
     * 
     * @param acrobatics new value acrobatics
     */
    public void setAcrobatics(String acrobatics) {
        this.acrobatics = acrobatics;
    }
    
    /**
     * Get the value of the animalHandl
     * 
     * @return the value of animalHandl
     */
    public String getAnimalHandling() {
        return animalHandl;
    }
    
    /**
     * Set the value of animalHandl
     * 
     * @param animalHandl new value animalHandl
     */
    public void setAnimalHandling(String animalHandl) {
        this.animalHandl = animalHandl;
    }
    
    /**
     * Get the value of the arcana
     * 
     * @return the value of arcana
     */
    public String getArcana() {
        return arcana;
    }
    
    /**
     * Set the value of arcana
     * 
     * @param arcana new value arcana
     */
    public void setArcana(String arcana) {
        this.arcana = arcana;
    }
    
    /**
     * Get the value of the athletics
     * 
     * @return the value of athletics
     */
    public String getAthletics() {
        return athletics;
    }
    
    /**
     * Set the value of athletics
     * 
     * @param athletics new value athletics
     */
    public void setAthletics(String athletics) {
        this.athletics = athletics;
    }
    
    /**
     * Get the value of the deception
     * 
     * @return the value of deception
     */
    public String getDeception() {
        return deception;
    }
    
    /**
     * Set the value of deception
     * 
     * @param deception new value deception
     */
    public void setDeception(String deception) {
        this.deception = deception;
    }
    
    /**
     * Get the value of the history
     * 
     * @return the value of history
     */
    public String getHistory() {
        return history;
    }
    
    /**
     * Set the value of history
     * 
     * @param history new value history
     */
    public void setHistory(String history) {
        this.history = history;
    }
    
    /**
     * Get the value of the insight
     * 
     * @return the value of insight
     */
    public String getInsight() {
        return insight;
    }
    
    /**
     * Set the value of insight
     * 
     * @param insight new value insight
     */
    public void setInsight(String insight) {
        this.insight = insight;
    }
    
    /**
     * Get the value of the intimidation
     * 
     * @return the value of intimidation
     */
    public String getIntimidation() {
        return intimidation;
    }
    
    /**
     * Set the value of intimidation
     * 
     * @param intimidation new value intimidation
     */
    public void setIntimidation(String intimidation) {
        this.intimidation = intimidation;
    }
    
    /**
     * Get the value of the investigation
     * 
     * @return the value of investigation
     */
    public String getInvestigation() {
        return investigation;
    }
    
    /**
     * Set the value of investigation
     * 
     * @param investigation new value investigation
     */
    public void setInvestigation(String investigation) {
        this.investigation = investigation;
    }
    
    /**
     * Get the value of the medicine
     * 
     * @return the value of medicine
     */
    public String getMedicine() {
        return medicine;
    }
    
    /**
     * Set the value of medicine
     * 
     * @param medicine new value medicine
     */
    public void setMedicine(String medicine) {
        this.medicine = medicine;
    }
    
    /**
     * Get the value of the nature
     * 
     * @return the value of nature
     */
    public String getNature() {
        return nature;
    }
    
    /**
     * Set the value of nature
     * 
     * @param nature new value nature
     */
    public void setNature(String nature) {
        this.nature = nature;
    }
    
    /**
     * Get the value of the perception
     * 
     * @return the value of perception
     */
    public String getPerception() {
        return perception;
    }
    
    /**
     * Set the value of perception
     * 
     * @param perception new value perception
     */
    public void setPerception(String perception) {
        this.perception = perception;
    }
    
    /**
     * Get the value of the performance
     * 
     * @return the value of performance
     */
    public String getPerformance() {
        return performance;
    }
    
    /**
     * Set the value of performance
     * 
     * @param performance new value performance
     */
    public void setPerformance(String performance) {
        this.performance = performance;
    }
    
    /**
     * Get the value of the persuasion
     * 
     * @return the value of persuasion
     */
    public String getPersuasion() {
        return persuasion;
    }
    
    /**
     * Set the value of persuasion
     * 
     * @param persuasion new value persuasion
     */
    public void setPersuasion(String persuasion) {
        this.persuasion = persuasion;
    }
    
    /**
     * Get the value of the religion
     * 
     * @return the value of religion
     */
    public String getReligion() {
        return religion;
    }
    
    /**
     * Set the value of religion
     * 
     * @param religion new value religion
     */
    public void setReligion(String religion) {
        this.religion = religion;
    }
    
    /**
     * Get the value of the sleightOfH
     * 
     * @return the value of sleightOfH
     */
    public String getSleightOfHand() {
        return sleightOfH;
    }
    
    /**
     * Set the value of sleightOfH
     * 
     * @param sleightOfH new value sleightOfH
     */
    public void setSleightOfHand(String sleightOfH) {
        this.sleightOfH = sleightOfH;
    }
    
    /**
     * Get the value of the stealth
     * 
     * @return the value of stealth
     */
    public String getStealth() {
        return stealth;
    }
    
    /**
     * Set the value of stealth
     * 
     * @param stealth new value stealth
     */
    public void setStealth(String stealth) {
        this.stealth = stealth;
    }
    
    /**
     * Get the value of the survival
     * 
     * @return the value of survival
     */
    public String getSurvival() {
        return survival;
    }
    
    /**
     * Set the value of survival
     * 
     * @param survival new value survival
     */
    public void setSurvival(String survival) {
        this.survival = survival;
    }
    /**
     * Get the value of proficiencies
     * 
     * @return the value of proficiencies
     */
    public String getProficiencies() {
        return proficiencies;
    }
    
    /**
     * Set the value of proficiencies
     * 
     * @param proficiencies new value of proficiencies
     */
    public void setProficiencies(String proficiencies) {
        this.proficiencies = proficiencies;
    }  
}
