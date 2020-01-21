/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Project;

/** This class stores and handles some of the character stats, features like
 *  race bonus points and skill proficiencies. It also returns combobox index
 *  values for UI when file has been loaded.
 *
 * @author Jutta Pietila
 */
public class CharacterStats {
    
    /* Final race names, compared to race names given from DnDCharacterCreator 
     * class.
     */
    final String DRAGONBORN = "Dragonborn";
    final String DWARF = "Dwarf";
    final String ELF = "Elf";
    final String GNOME = "Gnome";
    final String HALFELF = "Half-Elf";
    final String HALFLING = "Halfling";
    final String HALFORC = "Half-Orc";
    final String HUMAN = "Human";
    final String TIEFLING = "Tiefling";
    
    /* Final class names, that are used for comparing class names  from 
     * DnDCharacterCreator class.
     */
    final String BARBARIAN = "Barbarian";
    final String BARD = "Bard";
    final String CLERIC = "Cleric";
    final String DRUID = "Druid";
    final String FIGHTER = "Fighter";
    final String MONK = "Monk";
    final String PALADIN = "Paladin";
    final String RANGER = "Ranger";
    final String ROGUE = "Rogue";
    final String SORCERER = "Sorcerer";
    final String WARLOCK = "Warlock";
    final String WIZARD = "Wizard";
    
    // Background names
    final String ACOLYTE = "Acolyte";
    final String CHARLATAN = "Charlatan";
    final String CRIMINAL = "Criminal";
    final String ENTERTAINER = "Entertainer";
    final String FOLK_HERO = "Folk Hero";
    final String GUILD_ARTISAN = "Guild Artisan";
    final String HERMIT = "Hermit";
    final String NOBLE = "Noble";
    final String OUTLANDER = "Outlander";
    final String SAGE = "Sage";
    final String SAILOR = "Sailor";
    final String SOLDIER = "Soldier";
    final String URCHIN = "Urchin";
    
    // Skill names
    final String SKILL1 = "Acrobatics";
    final String SKILL2 = "Animal Handling";
    final String SKILL3 = "Arcana";
    final String SKILL4 = "Athletics";
    final String SKILL5 = "Deception";
    final String SKILL6 = "History";
    final String SKILL7 = "Insight";
    final String SKILL8 = "Intimidation";
    final String SKILL9 = "Investigation";
    final String SKILL10 = "Medicine";
    final String SKILL11 = "Nature";
    final String SKILL12 = "Perception";
    final String SKILL13 = "Performance";
    final String SKILL14 = "Persuasion";
    final String SKILL15 = "Religion";
    final String SKILL16 = "Sleight Of Hand";
    final String SKILL17 = "Stealth";
    final String SKILL18 = "Survival";    
    
    // Alignment names
    final String ALIGNMENT1 = "Lawful good (LG)";
    final String ALIGNMENT2 = "Neutral good (NG)";
    final String ALIGNMENT3 = "Chaotic good (CG)";
    final String ALIGNMENT4 = "Lawful neutral (LN)";
    final String ALIGNMENT5 = "Neutral (N)";
    final String ALIGNMENT6 = "Chaotic neutral (CN)";
    final String ALIGNMENT7 = "Lawful evil (LE)";
    final String ALIGNMENT8 = "Neutral evil (NE)";
    final String ALIGNMENT9 = "Chaotic evil (CE)";
    
    // Modifier value
    int modifier = 0;
 
    // Race bonus points array
    int[] bonusPoints = {0, 0, 0, 0, 0, 0};
    
    // Skill points array
    String[] skillPoints = new String[18];
    // Class proficiencies
    int classSkill = 0;
    
    // The amount of proficiency skills
    int amountOfSkills = 0;
    
    // Background proficiency skills array
    String[] bGSkills = new String[2];
    // Background proficiencies
    int bGSkill = 0;
    
    // Combobox index values
    int raceIndex = 0;
    int classIndex = 0;
    int bGIndex = 0;
    int alignmentIndex = 0;   
    
    /**
     * Get the value of the attribute specified by index number.
     * Bonus poinst depend on the character's race, each race has different
     * race bonus points in the abilities.
     * @param id the index number of the wanted attribute (STR/DEX/CON/INT/WIS/CHA)
     * @return the numerical value of the specified attribute or -1 in case of error.
     */
    public int getBonusPoints(int id) {
        if (id >= 0 && id < 6) {
            return bonusPoints[id];
        }
        else
            return -1;
    }
    
    /**
     * Method that resets the race bonus points.
     */
    public void resetBonusPoints() {
        bonusPoints[0] = 0;
        bonusPoints[1] = 0;
        bonusPoints[2] = 0;
        bonusPoints[3] = 0;
        bonusPoints[4] = 0;
        bonusPoints[5] = 0;
    }
    /**
     * Sets the value of bonuspoints[id]
     * @param race value of race
     */
    public void setBonusPoints(String race) {
        resetBonusPoints();
        switch (race) {
            case DRAGONBORN:
                bonusPoints[0] = 2;
                bonusPoints[5] = 1;
                break;
            case DWARF:
                bonusPoints[2] = 2;
                break;
            case ELF:
                bonusPoints[1] = 2;
                break;
            case GNOME:
                bonusPoints[3] = 2;
                break;
            case HALFELF:
                bonusPoints[5] = 2;
                break;
            case HALFLING:
                bonusPoints[1] = 2;
                break;
            case HALFORC:
                bonusPoints[0] = 2;
                bonusPoints[2] = 1;
                break;
            case HUMAN:
                bonusPoints[0] = 1;
                bonusPoints[1] = 1;
                bonusPoints[2] = 1;
                bonusPoints[3] = 1;
                bonusPoints[4] = 1;
                bonusPoints[5] = 1;
                break;
            case TIEFLING:
                bonusPoints[3] = 1;
                bonusPoints[5] = 2;
                break;
            default:
                break;
        }
    }
    /**
     * Get the value of the modifier points.
     * 
     * Modifier points are counted from the total ability point scores. The modifier
     * points are determined by the following way:
     * Score --------- Modifier
     *   1       /       -5
     *   2-3     /       -4
     *   4-5     /       -3
     *   6-7     /       -2
     *   8-9     /       -1
     *   10-11   /        0
     *   12-13   /        1
     *   14-15   /        2
     *   16-17   /        3
     *   18-19   /        4
     *   20-21   /        5
     * etc. in higher character levels with ability point updates
     * 
     * @return the value of modifier
     */
    public int getModifierPoints() {
        if (modifier > -5 && modifier < 6) {
            return modifier;
        }
        else
            return 0;
    }
    /**
     * Set the value of modifier
     * @param total the value of total ability score
     */
    public void setModifierPoints(int total) {
        if (total < 3) {
            modifier = -5;
        }
        if (total == 3) {
            modifier = -4;
        }
        if (total == 4 || total == 5) {
            modifier = -3;
        }
        if (total == 6 || total == 7) {
            modifier = -2;
        }
        if (total == 8 || total == 9) {
            modifier = -1;
        }
        if (total == 10 || total == 11) {
            modifier = 0;
        }
        if (total == 12 || total == 13) {
            modifier = 1;
        }
        if (total == 14 || total == 15) {
            modifier = 2;
        }
        if (total == 16 || total == 17) {
            modifier = 3;
        }
        if (total == 18 || total == 19) {
            modifier = 4;
        }
        if (total == 20 || total == 21) {
            modifier = 5;
        }      
    }
    
    /**
     * Get the value of classSkill.
     * 
     * Every class has different class skill proficiencies. This method returns
     * a list of Strings, available class skill proficiencies, for the DnDCharacterCreator
     * class.
     * 
     * @param classSkill the new value of classSkill
     * @return classSkill tha value of skillPoints[classSkill]
     */
    public String getClassSkills(int classSkill) {
        if (classSkill >= 0 && classSkill < 18 && (skillPoints[classSkill] != null)) {
           return skillPoints[classSkill]; 
        }
        else
            return "";                
    }
       
    /**
     * Set the values of skills and skill indexes
     * @param className the value of class
     */
    public void setClassSkills(String className) {
        switch (className) {
            case BARBARIAN:
                skillPoints[0] = SKILL2;
                skillPoints[1] = SKILL4;
                skillPoints[2] = SKILL8;
                skillPoints[3] = SKILL11;
                skillPoints[4] = SKILL12;
                skillPoints[5] = SKILL18;
                break;
            case BARD:
                skillPoints[0] = SKILL1;
                skillPoints[1] = SKILL2;
                skillPoints[2] = SKILL3;
                skillPoints[3] = SKILL4;
                skillPoints[4] = SKILL5;
                skillPoints[5] = SKILL6;
                skillPoints[6] = SKILL7;
                skillPoints[7] = SKILL8;
                skillPoints[8] = SKILL9;
                skillPoints[9] = SKILL10;
                skillPoints[10] = SKILL11;
                skillPoints[11] = SKILL12;
                skillPoints[12] = SKILL13;
                skillPoints[13] = SKILL14;
                skillPoints[14] = SKILL15;
                skillPoints[15] = SKILL16;
                skillPoints[16] = SKILL17;
                skillPoints[17] = SKILL18;
                break;
            case CLERIC:
                skillPoints[0] = SKILL6;
                skillPoints[1] = SKILL7;
                skillPoints[2] = SKILL10;
                skillPoints[3] = SKILL14;
                skillPoints[4] = SKILL15;
                break;
            case DRUID:
                skillPoints[0] = SKILL2;
                skillPoints[1] = SKILL3;
                skillPoints[2] = SKILL7;
                skillPoints[3] = SKILL10;
                skillPoints[4] = SKILL11;
                skillPoints[5] = SKILL12;
                skillPoints[6] = SKILL15;
                skillPoints[7] = SKILL18;
                break;
            case FIGHTER:
                skillPoints[0] = SKILL1;
                skillPoints[1] = SKILL2;
                skillPoints[2] = SKILL4;
                skillPoints[3] = SKILL6;
                skillPoints[4] = SKILL7;
                skillPoints[5] = SKILL8;
                skillPoints[6] = SKILL12;
                skillPoints[7] = SKILL18;
                break;
            case MONK:
                skillPoints[0] = SKILL1;
                skillPoints[1] = SKILL4;
                skillPoints[2] = SKILL6;
                skillPoints[3] = SKILL7;
                skillPoints[4] = SKILL15;
                skillPoints[5] = SKILL17;
                break;
            case PALADIN:
                skillPoints[0] = SKILL4;
                skillPoints[1] = SKILL7;
                skillPoints[2] = SKILL8;
                skillPoints[3] = SKILL10;
                skillPoints[4] = SKILL14;
                skillPoints[5] = SKILL15;
                break;
            case RANGER:
                skillPoints[0] = SKILL2;
                skillPoints[1] = SKILL4;
                skillPoints[2] = SKILL7;
                skillPoints[3] = SKILL9;
                skillPoints[4] = SKILL11;
                skillPoints[5] = SKILL12;
                skillPoints[6] = SKILL17;
                skillPoints[7] = SKILL18;
                break;
            case ROGUE:
                skillPoints[0] = SKILL1;
                skillPoints[1] = SKILL4;
                skillPoints[2] = SKILL5;
                skillPoints[3] = SKILL7;
                skillPoints[4] = SKILL8;
                skillPoints[5] = SKILL9;
                skillPoints[6] = SKILL12;
                skillPoints[7] = SKILL13;
                skillPoints[8] = SKILL14;
                skillPoints[9] = SKILL16;
                skillPoints[10] = SKILL17;
                break;
            case SORCERER:
                skillPoints[0] = SKILL3;
                skillPoints[1] = SKILL5;
                skillPoints[2] = SKILL7;
                skillPoints[3] = SKILL8;
                skillPoints[4] = SKILL14;
                skillPoints[5] = SKILL15;
                break;
            case WARLOCK:
                skillPoints[0] = SKILL3;
                skillPoints[1] = SKILL5;
                skillPoints[2] = SKILL6;
                skillPoints[3] = SKILL8;
                skillPoints[4] = SKILL9;
                skillPoints[5] = SKILL11;
                skillPoints[6] = SKILL15;
                break;
            case WIZARD:
                skillPoints[0] = SKILL3;
                skillPoints[1] = SKILL6;
                skillPoints[2] = SKILL7;
                skillPoints[3] = SKILL9;
                skillPoints[4] = SKILL10;
                skillPoints[5] = SKILL15;
                break;
            default:
                break;
        }
    }
    /**
     * Get the value of amountOfSkills.
     * 
     * The amount of available class proficiency skills is determined by
     * the class. Many classes can have 2 skills, some 3 and one can choose 4.
     * 
     * @return the value of amountOfSkills
     */
    public int getAmountOfClassSkills() {
        if (amountOfSkills != 0) {
            return amountOfSkills;
        }
        else
            return 0;
    }
    /**
     * Set the value of amountOfSkills.
     * 
     * @param className the value of className
     */
    public void setAmountOfClassSkills(String className) {
        switch(className) {
            case BARBARIAN:  case CLERIC: case DRUID: case FIGHTER: case MONK:
            case PALADIN: case SORCERER: case WARLOCK: case WIZARD:
                amountOfSkills = 2;
                break;
            case BARD: case RANGER:
                amountOfSkills = 3;
                break;
            case ROGUE:
                amountOfSkills = 4;
                break;
            default:
                break;                
        }
    }
    
    /** 
     * Get the value of bGSkill.
     * 
     * Every background option offers two specific proficiency skills for a 
     * character.
     * 
     * @param bGSkill new value of bGSkill
     * @return the value of bGSkills[bGSkill]
     */
    public String getBackgroundSkills(int bGSkill) {
        if (bGSkill >= 0) {
           return bGSkills[bGSkill]; 
        }
        else
            return "";  
    }
    
    /**
     * Set the value of bGSkill[], bGSkillIndex[]
     * @param background the value of background
     */
    public void setBackgroundSkills(String background) {
        switch(background) {
            case ACOLYTE:
                bGSkills[0] = SKILL7;
                bGSkills[1] = SKILL15;
                break;
            case CHARLATAN:
                bGSkills[0] = SKILL5;
                bGSkills[1] = SKILL16;
                break;
            case CRIMINAL:
                bGSkills[0] = SKILL5;
                bGSkills[1] = SKILL17;
                break;
            case ENTERTAINER:
                bGSkills[0] = SKILL1;
                bGSkills[1] = SKILL13;
                break;
            case FOLK_HERO:
                bGSkills[0] = SKILL2;
                bGSkills[1] = SKILL18;
                break;
            case GUILD_ARTISAN:
                bGSkills[0] = SKILL7;
                bGSkills[1] = SKILL14;
                break;
            case HERMIT:
                bGSkills[0] = SKILL10;
                bGSkills[1] = SKILL15;
                break;
            case NOBLE:
                bGSkills[0] = SKILL6;
                bGSkills[1] = SKILL14;
                break;
            case OUTLANDER:
                bGSkills[0] = SKILL4;
                bGSkills[1] = SKILL18;
                break;
            case SAGE:
                bGSkills[0] = SKILL3;
                bGSkills[1] = SKILL6;
                break;
            case SAILOR:
                bGSkills[0] = SKILL4;
                bGSkills[1] = SKILL12;
                break;
            case SOLDIER:
                bGSkills[0] = SKILL4;
                bGSkills[1] = SKILL8;
                break;
            case URCHIN:
                bGSkills[0] = SKILL16;
                bGSkills[1] = SKILL17;
                break;              
        }
    }
    
    /**
     * Get the value of raceIndex.
     * 
     * This index value is used to help the DnDCharacterCreator class to define the
     * right value of the comboBoxRace when loading a character from a file.
     * 
     * @return the value of raceIndex
     */
    public int getComboBoxRaceValue() {
        if (raceIndex >= 0) {
           return raceIndex; 
        }
        /* If there has been changes in the txt file and this class does not
         * recognize the name of the race given, the class is set to default
         * value (Dragonborn)
         */ 
        else
            return 0;       
    }
    
    /**
     * Set the value of raceIndex
     * @param race the value of race
     */
    public void setComboBoxRaceValue(String race) {
        switch(race) {
            case DRAGONBORN:
                raceIndex = 0;
                break;
            case DWARF:
                raceIndex = 1;
                break;
            case ELF:
                raceIndex = 2;
                break;
            case GNOME:
                raceIndex = 3;
                break;
            case HALFELF:
                raceIndex = 4;
                break;
            case HALFLING:
                raceIndex = 5;
                break;
            case HALFORC:
                raceIndex = 6;
                break;
            case HUMAN:
                raceIndex = 7;
                break;
            case TIEFLING:
                raceIndex = 8;
                break;
            default:
                break;             
        }   
    }
    
    /**
     * Get the value of classIndex.
     * 
     * This index value is used to help the DnDCharacterCreator class to define
     * the right value of the comboBoxClass when loading a character from a file.
     * 
     * @return the value of classIndex
     */
    public int getComboBoxClassValue() {
        if (classIndex >= 0) {
           return classIndex; 
        }
        else
            /* If there has been changes in the txt file and this class does not
             * recognize the name of the class given, the class is set to default
             * value (Barbarian)
             */          
            return 0;      
    }
    
    /**
     * Set the value of classIndex
     * @param characterClass the value of characterClass
     */
    public void setComboBoxClassValue(String characterClass) {
        switch(characterClass) {
            case BARBARIAN:
                classIndex = 0;
                break;
            case BARD:
                classIndex = 1;
                break;
            case CLERIC:
                classIndex = 2;
                break;
            case DRUID:
                classIndex = 3;
                break;
            case FIGHTER:
                classIndex = 4;
                break;
            case MONK:
                classIndex = 5;
                break;
            case PALADIN:
                classIndex = 6;
                break;
            case RANGER:
                classIndex = 7;
                break;
            case ROGUE:
                classIndex = 8;
                break;
            case SORCERER:
                classIndex = 9;
                break;
            case WARLOCK:
                classIndex = 10;
                break;
            case WIZARD:
                classIndex = 11;
                break;
            default:
                break;
        }   
    }
    
    /**
     * Get the value of bGIndex.
     * 
     * This index value is used to help the DnDCharacterCreator class to define
     * the right value of the comboBoxBackground when loading a character from a file.
     * 
     * @return the value of bGIndex
     */
    public int getComboBoxBGValue() {
        if (bGIndex >= 0) {
           return bGIndex; 
        }
        /* If there has been changes in the txt file and this class does not
         * recognize the name of the background given, the class is set to default
         * value (Acolyte)
         */ 
        else
            return 0;       
    }
    
    /**
     * Set the value of bGIndex
     * 
     * @param background the value of background
     */
    public void setComboBoxBGValue(String background) {
        switch(background) {
            case ACOLYTE:
                bGIndex = 0;
                break;
            case CHARLATAN:
                bGIndex = 1;
                break;
            case CRIMINAL:
                bGIndex = 2;
                break;
            case ENTERTAINER:
                bGIndex = 3;
                break;
            case FOLK_HERO:
                bGIndex = 4;
                break;
            case GUILD_ARTISAN:
                bGIndex = 5;
                break;
            case HERMIT:
                bGIndex = 6;
                break;
            case NOBLE:
                bGIndex = 7;
                break;
            case OUTLANDER:
                bGIndex = 8;
                break;
            case SAGE:
                bGIndex = 9;
                break;
            case SAILOR:
                bGIndex = 10;
                break;
            case SOLDIER:
                bGIndex = 11;
                break;
            case URCHIN:
                bGIndex = 12;
                break;             
        }
    }
    
    /**
     * Get the value of alignmentIndex.
     * 
     * This index value is used to help the DnDCharacterCreator class to define
     * the right value of the comboBoxAlignment when loading a character from a file.
     * 
     * @return the value of alignmentIndex
     */
    public int getComboBoxAlignmentValue() {
        if (alignmentIndex >= 0) {
           return alignmentIndex; 
        }
        /* If there has been changes in the txt file and this class does not
         * recognize the name of the alignment given, the class is set to default
         * value (Lawful good)
         */ 
        else
            return 0;       
    }
    
    /**
     * Set the value of alignmentIndex
     * 
     * @param alignment the value of alignment
     */
    public void setComboBoxAlignmentValue(String alignment) {
        switch(alignment) {
            case ALIGNMENT1:
                alignmentIndex = 0;
                break;
            case ALIGNMENT2:
                alignmentIndex = 1;
                break;
            case ALIGNMENT3:
                alignmentIndex = 2;
                break;
            case ALIGNMENT4:
                alignmentIndex = 3;
                break;
            case ALIGNMENT5:
                alignmentIndex = 4;
                break;
            case ALIGNMENT6:
                alignmentIndex = 5;
                break;
            case ALIGNMENT7:
                alignmentIndex = 6;
                break;
            case ALIGNMENT8:
                alignmentIndex = 7;
                break;
            case ALIGNMENT9:
                alignmentIndex = 8;
                break;
            default:
                break;      
        }   
    }
}

