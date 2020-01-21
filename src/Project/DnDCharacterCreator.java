/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Project;

import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.Document;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;


/** 
 * This class manages the user interface of the Dungeong & Dragon Character
 * Creator. The class manages various UI components, saves and gives forward data.
 * 
 * The class gets a lot of DnD stats information from the CharacterStats class, 
 * which helps getting the various stats correctly also in the UI. When saving
 * a character, an object, Character is created, and Character class's various
 * set() and get() methods are used, as the data of a character can be stored into
 * a txt file, which can be loaded in UI afterwards. Additional variables can be
 * found after the methods, before listed java.swing components.
 * 
 * @author Jutta Pietila
 */

 /*  
  * ----------------------------------------------------------------------------
  * Explanations for some of the terms in use
  * 
  * Ability points: Users have to count their character's ability point scores
  * when they are creating a character. The abilities are; strength, dexterity,
  * constitution, intelligence, wisdom and charisma, and all of them have total 
  * score and a modifier score. In this program, the user is instructed to define
  * the ability scoring by rolling 4 6-sided dices and recording the total of the
  * 3 highest dice, doing this 6 times in total, and then he has to place the 
  * points for each ability as he likes to. In UI, abilities are managed with
  * spinners, and the race bonuses are added to the total ability point score.
  * 
  * Modifier points: Each ability has a modifier, which effects directly to the skill
  * points (bonuses) of a character. If for example, a character has 2 dexterity
  * modifier points, the character will have +2 points in all skills, that are based
  * on dexterity. In practice, in the game these modifiers act as a bonus or negative
  * effect in the game's encounters (e.g. player has to do a perception check, he
  * rolls the 20-sided dice, and gets +2 for his score due to the dexterity modifier
  * +2). The modifier can also be negative, so that character gets -x points when
  * rolling a dice in an encounter.
  * 
  * Class and background skill proficiencies: Proficiency skills are like specializations
  * for specific skills, and character gets +2 points for the modifier of these skills
  * at level 1. The class and background of the character defines, which and how 
  * many proficiencies the character may have. The background skill proficiencies 
  * are always two specific skills, and they cannot be chosen, but class skills
  * are chosen by the player and the amount of the skills varies depending on the
  * class. The +2 points is then added to the modifer of the skill.
  *
  * 
  */
public class DnDCharacterCreator extends javax.swing.JFrame {

    /**
     * Creates new form DnDCharacterCreator
     */
    public DnDCharacterCreator() {
        
        initComponents();
        /* In case of saving/loading character or viewing skills without changing
         * abilities, they are set to be 0 by default.
         */       
        totalSTR.setText(Integer.toString(defaultValue));
        totalDEX.setText(Integer.toString(defaultValue));
        totalCON.setText(Integer.toString(defaultValue));
        totalINT.setText(Integer.toString(defaultValue));
        totalWIS.setText(Integer.toString(defaultValue));
        totalCHA.setText(Integer.toString(defaultValue));
        modifierSTR.setText("0");
        modifierDEX.setText("0");
        modifierCON.setText("0");
        modifierINT.setText("0");
        modifierWIS.setText("0");
        modifierCHA.setText("0");
        
        /* If user changes the race, the race bonuses in the ability points must
         * be updated. The user is asked to check his points again and save them, 
         * since a change of race might have a big influence to the total point
         * score of abilities, and their modifiers.
         */
        comboBoxRace.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (loadedFile == false) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        informChangedAbilities();
                    } 
                }                
            }       
        });
        /* If user changes the class or background, the proficiencies are different
         * from the earlier. It's necessary to ask the user to choose the proficiencies
         * again, so that they match to the background/class
         *
         */
        comboBoxClass.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (loadedFile == false) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        informChoosingProficiencies();
                        resetProficiencies();
                        countSkillPoints();
                    }
                }
            }
        });
        
        comboBoxBackground.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (loadedFile == false) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        informChoosingProficiencies();
                        resetProficiencies();
                        countSkillPoints();
                    }
                }
            }
        });
        
        dialogAbilities.pack();
        dialogSkills.pack();
        dialogStory.pack();
        halfElfStats.pack();
        
        
         /* The undo-redo function in the Background Story dialog is controlled
          * mainly here, because  of the visibility of the Document story and UndoManager
          * undo. The undo-redo works with keyboard shortcuts (ctrl + Z/Y) and
          * "Undo" and "Redo" buttons which have their own ActionPerformed methods.
          * The keyboard shotrcut controlled undo/redo events are managed here.
          */
        
        textAreaStory.setText("");
        this.story = textAreaStory.getDocument();
        undo = new UndoManager();
        
        // Listens for undo and redo events in textAreaStory
        story.addUndoableEditListener((UndoableEditEvent evt) -> {
            undo.addEdit(evt.getEdit());  
        });
        
        // Creates an undo action and adds it to textAreaStory
        textAreaStory.getActionMap().put("Undo", new AbstractAction("Undo") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (undo.canUndo()) {
                        undo.undo();
                    }
                } catch (CannotUndoException e) {
                }
            }
        });
        
        // Binding the undo action to keyboard shortcut ctrl + Z
        textAreaStory.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
        
        // Creating a redo action and adding it to the textAreaStory
        textAreaStory.getActionMap().put("Redo", new AbstractAction("Redo") {
           @Override
           public void actionPerformed(ActionEvent evt) {
               try {
                   if (undo.canRedo()) {
                       undo.redo();
                   }
               } catch (CannotRedoException e) {
               }
           } 
        });
        
        // Binding the redo action to ctrl + Y
        textAreaStory.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");
        
        // At first, "Save" is disabled, the user needs to start saving with Save As
        menuSave.setEnabled(false);
        buttonSave.setEnabled(false);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fontSButtonGroup = new javax.swing.ButtonGroup();
        dialogAbilities = new javax.swing.JDialog();
        labelAbilities = new javax.swing.JLabel();
        labelCR = new javax.swing.JLabel();
        labelInstructions = new javax.swing.JLabel();
        instructionsOne = new javax.swing.JLabel();
        instructionsTwo = new javax.swing.JLabel();
        instructionsThree = new javax.swing.JLabel();
        labelStrenght = new javax.swing.JLabel();
        labelDexterity = new javax.swing.JLabel();
        labelConstitution = new javax.swing.JLabel();
        labelIntelligence = new javax.swing.JLabel();
        labelWisdom = new javax.swing.JLabel();
        labelCharisma = new javax.swing.JLabel();
        spinnerSTR = new javax.swing.JSpinner();
        spinnerDEX = new javax.swing.JSpinner();
        spinnerCON = new javax.swing.JSpinner();
        spinnerINT = new javax.swing.JSpinner();
        spinnerWIS = new javax.swing.JSpinner();
        spinnerCHA = new javax.swing.JSpinner();
        labelPoints = new javax.swing.JLabel();
        labelRB = new javax.swing.JLabel();
        bonusSTR = new javax.swing.JLabel();
        bonusDEX = new javax.swing.JLabel();
        bonusCON = new javax.swing.JLabel();
        bonusINT = new javax.swing.JLabel();
        bonusWIS = new javax.swing.JLabel();
        bonusCHA = new javax.swing.JLabel();
        labelTotal = new javax.swing.JLabel();
        totalSTR = new javax.swing.JLabel();
        totalDEX = new javax.swing.JLabel();
        totalCON = new javax.swing.JLabel();
        totalINT = new javax.swing.JLabel();
        totalWIS = new javax.swing.JLabel();
        totalCHA = new javax.swing.JLabel();
        modifierSTR = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        modifierDEX = new javax.swing.JLabel();
        modifierCON = new javax.swing.JLabel();
        modifierINT = new javax.swing.JLabel();
        modifierWIS = new javax.swing.JLabel();
        modifierCHA = new javax.swing.JLabel();
        buttonASC = new javax.swing.JButton();
        buttonACancel = new javax.swing.JButton();
        dialogSkills = new javax.swing.JDialog();
        labelSkills = new javax.swing.JLabel();
        labelProficiency = new javax.swing.JLabel();
        labelAcro = new javax.swing.JLabel();
        labelAnim = new javax.swing.JLabel();
        labelArca = new javax.swing.JLabel();
        labelAthl = new javax.swing.JLabel();
        labelDece = new javax.swing.JLabel();
        labelHist = new javax.swing.JLabel();
        labelInsi = new javax.swing.JLabel();
        labelInti = new javax.swing.JLabel();
        labelInve = new javax.swing.JLabel();
        labelMedi = new javax.swing.JLabel();
        labelNatu = new javax.swing.JLabel();
        labelPerc = new javax.swing.JLabel();
        labelPerf = new javax.swing.JLabel();
        labelPers = new javax.swing.JLabel();
        labelReli = new javax.swing.JLabel();
        labelSlei = new javax.swing.JLabel();
        labelStea = new javax.swing.JLabel();
        labelSurv = new javax.swing.JLabel();
        modifierAcro = new javax.swing.JLabel();
        modifierAnim = new javax.swing.JLabel();
        modifierArca = new javax.swing.JLabel();
        modifierAthl = new javax.swing.JLabel();
        modifierDece = new javax.swing.JLabel();
        modifierHist = new javax.swing.JLabel();
        modifierInsi = new javax.swing.JLabel();
        modifierInti = new javax.swing.JLabel();
        modifierInve = new javax.swing.JLabel();
        modifierMedi = new javax.swing.JLabel();
        modifierNatu = new javax.swing.JLabel();
        modifierPerc = new javax.swing.JLabel();
        modifierPerf = new javax.swing.JLabel();
        modifierPers = new javax.swing.JLabel();
        modifierReli = new javax.swing.JLabel();
        modifierSlei = new javax.swing.JLabel();
        modifierStea = new javax.swing.JLabel();
        modifierSurv = new javax.swing.JLabel();
        labelClassProficiencies = new javax.swing.JLabel();
        buttonSOk = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        textPaneSkillsC = new javax.swing.JTextPane();
        labelSBackg = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        textPaneSkillsB = new javax.swing.JTextPane();
        checkB1 = new javax.swing.JCheckBox();
        checkB2 = new javax.swing.JCheckBox();
        checkB3 = new javax.swing.JCheckBox();
        checkB4 = new javax.swing.JCheckBox();
        checkB5 = new javax.swing.JCheckBox();
        checkB6 = new javax.swing.JCheckBox();
        checkB7 = new javax.swing.JCheckBox();
        checkB8 = new javax.swing.JCheckBox();
        checkB9 = new javax.swing.JCheckBox();
        checkB10 = new javax.swing.JCheckBox();
        checkB11 = new javax.swing.JCheckBox();
        checkB12 = new javax.swing.JCheckBox();
        checkB13 = new javax.swing.JCheckBox();
        checkB14 = new javax.swing.JCheckBox();
        checkB15 = new javax.swing.JCheckBox();
        checkB16 = new javax.swing.JCheckBox();
        checkB17 = new javax.swing.JCheckBox();
        checkB18 = new javax.swing.JCheckBox();
        dialogStory = new javax.swing.JDialog();
        labelDialogStoryHeader = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        textAreaStory = new javax.swing.JTextArea();
        buttonSaveS = new javax.swing.JButton();
        buttonCancelS = new javax.swing.JButton();
        buttonUndo = new javax.swing.JButton();
        buttonRedo = new javax.swing.JButton();
        halfElfStats = new javax.swing.JDialog();
        halfElfInfoOne = new javax.swing.JLabel();
        halfElfInfoTwo = new javax.swing.JLabel();
        halfElfInfoThree = new javax.swing.JLabel();
        halfElfSTR = new javax.swing.JCheckBox();
        halfElfDEX = new javax.swing.JCheckBox();
        halfElfCON = new javax.swing.JCheckBox();
        halfElfINT = new javax.swing.JCheckBox();
        halfElfWIS = new javax.swing.JCheckBox();
        buttonSaveClose = new javax.swing.JButton();
        header = new javax.swing.JLabel();
        labelName = new javax.swing.JLabel();
        textFieldName = new javax.swing.JTextField();
        labelRace = new javax.swing.JLabel();
        labelClass = new javax.swing.JLabel();
        comboBoxClass = new javax.swing.JComboBox<>();
        labelBackground = new javax.swing.JLabel();
        comboBoxBackground = new javax.swing.JComboBox<>();
        labelBasicI = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableBasicI = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        comboBoxAlignment = new javax.swing.JComboBox<>();
        buttonAbilities = new javax.swing.JButton();
        buttonSkills = new javax.swing.JButton();
        abilityPanel = new javax.swing.JPanel();
        labelSTR = new javax.swing.JLabel();
        labelDEX = new javax.swing.JLabel();
        labelCON = new javax.swing.JLabel();
        labelINT = new javax.swing.JLabel();
        labelWIS = new javax.swing.JLabel();
        labelCHA = new javax.swing.JLabel();
        buttonPicture = new javax.swing.JButton();
        labelPersonality = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        textAPersonality = new javax.swing.JTextArea();
        labelIdeals = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        textAIdeals = new javax.swing.JTextArea();
        labelBonds = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        textABonds = new javax.swing.JTextArea();
        labelFlaws = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        textAFlaws = new javax.swing.JTextArea();
        labelFeatsTraits = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        textAFeatsTraits = new javax.swing.JTextArea();
        buttonStory = new javax.swing.JButton();
        buttonSave = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();
        comboBoxRace = new javax.swing.JComboBox<>();
        picture1 = new Project.Picture();
        buttonSaveAs = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuNewC = new javax.swing.JMenuItem();
        menuLoadC = new javax.swing.JMenuItem();
        menuSave = new javax.swing.JMenuItem();
        menuSaveAs = new javax.swing.JMenuItem();
        menuExit = new javax.swing.JMenuItem();
        disableEditSettings = new javax.swing.JMenu();
        menuFont = new javax.swing.JMenu();
        font12 = new javax.swing.JRadioButtonMenuItem();
        font14 = new javax.swing.JRadioButtonMenuItem();
        font16 = new javax.swing.JRadioButtonMenuItem();
        menuDisableE = new javax.swing.JCheckBoxMenuItem();
        menuResetC = new javax.swing.JMenuItem();

        dialogAbilities.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        dialogAbilities.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                dialogAbilitiesWindowClosing(evt);
            }
        });

        labelAbilities.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        labelAbilities.setText("Abilities");

        labelCR.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelCR.setText("Character's race:");

        labelInstructions.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelInstructions.setText("Instructions:");

        instructionsOne.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        instructionsOne.setText("Generate your character's ability points by rolling four 6-sided dices and recording the total of the highest");

        instructionsTwo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        instructionsTwo.setText("three dice on a piece of paper. Repeat this five more times, so that you have six numbers. Assign the ");

        instructionsThree.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        instructionsThree.setText("numbers to the different abilities. The ability scores determine your character's ability modifiers.");

        labelStrenght.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelStrenght.setText("Strength");

        labelDexterity.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelDexterity.setText("Dexterity");

        labelConstitution.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelConstitution.setText("Constitution");

        labelIntelligence.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelIntelligence.setText("Intelligence");

        labelWisdom.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelWisdom.setText("Wisdom");

        labelCharisma.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelCharisma.setText("Charisma");

        spinnerSTR.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerSTRStateChanged(evt);
            }
        });

        spinnerDEX.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerDEXStateChanged(evt);
            }
        });

        spinnerCON.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerCONStateChanged(evt);
            }
        });

        spinnerINT.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerINTStateChanged(evt);
            }
        });

        spinnerWIS.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerWISStateChanged(evt);
            }
        });

        spinnerCHA.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerCHAStateChanged(evt);
            }
        });

        labelPoints.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelPoints.setText("Points");

        labelRB.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelRB.setText("Racial bonus");

        bonusSTR.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        bonusSTR.setText("0");

        bonusDEX.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        bonusDEX.setText("0");

        bonusCON.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        bonusCON.setText("0");

        bonusINT.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        bonusINT.setText("0");

        bonusWIS.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        bonusWIS.setText("0");

        bonusCHA.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        bonusCHA.setText("0");

        labelTotal.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelTotal.setText("Total ");

        totalSTR.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        totalSTR.setText("0");

        totalDEX.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        totalDEX.setText("0");

        totalCON.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        totalCON.setText("0");

        totalINT.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        totalINT.setText("0");

        totalWIS.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        totalWIS.setText("0");

        totalCHA.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        totalCHA.setText("0");

        modifierSTR.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        modifierSTR.setText("0");

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setText("Modifier");

        modifierDEX.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        modifierDEX.setText("0");

        modifierCON.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        modifierCON.setText("0");

        modifierINT.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        modifierINT.setText("0");

        modifierWIS.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        modifierWIS.setText("0");

        modifierCHA.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        modifierCHA.setText("0");

        buttonASC.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        buttonASC.setText("Save and close");
        buttonASC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonASCActionPerformed(evt);
            }
        });

        buttonACancel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        buttonACancel.setText("Cancel changes");
        buttonACancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonACancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout dialogAbilitiesLayout = new javax.swing.GroupLayout(dialogAbilities.getContentPane());
        dialogAbilities.getContentPane().setLayout(dialogAbilitiesLayout);
        dialogAbilitiesLayout.setHorizontalGroup(
            dialogAbilitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogAbilitiesLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(dialogAbilitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(instructionsThree)
                    .addComponent(instructionsOne)
                    .addComponent(labelInstructions)
                    .addComponent(labelCR)
                    .addComponent(labelAbilities)
                    .addGroup(dialogAbilitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, dialogAbilitiesLayout.createSequentialGroup()
                            .addGroup(dialogAbilitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(labelStrenght)
                                .addComponent(labelDexterity)
                                .addComponent(labelConstitution)
                                .addComponent(labelIntelligence)
                                .addComponent(labelWisdom)
                                .addComponent(labelCharisma))
                            .addGap(41, 41, 41)
                            .addGroup(dialogAbilitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(labelPoints)
                                .addComponent(spinnerSTR, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(spinnerDEX, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(spinnerCON, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(spinnerINT, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(spinnerWIS, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(spinnerCHA, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(36, 36, 36)
                            .addGroup(dialogAbilitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(labelRB)
                                .addGroup(dialogAbilitiesLayout.createSequentialGroup()
                                    .addGap(20, 20, 20)
                                    .addGroup(dialogAbilitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(bonusDEX)
                                        .addComponent(bonusSTR)
                                        .addComponent(bonusCON)
                                        .addComponent(bonusINT)
                                        .addComponent(bonusWIS)
                                        .addComponent(bonusCHA))))
                            .addGap(34, 34, 34)
                            .addGroup(dialogAbilitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(labelTotal)
                                .addGroup(dialogAbilitiesLayout.createSequentialGroup()
                                    .addGap(5, 5, 5)
                                    .addGroup(dialogAbilitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(dialogAbilitiesLayout.createSequentialGroup()
                                            .addGroup(dialogAbilitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(totalSTR)
                                                .addComponent(totalDEX)
                                                .addComponent(totalCON)
                                                .addComponent(totalINT)
                                                .addComponent(totalWIS)
                                                .addComponent(totalCHA))
                                            .addGap(54, 54, 54)
                                            .addGroup(dialogAbilitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jLabel1)
                                                .addGroup(dialogAbilitiesLayout.createSequentialGroup()
                                                    .addGap(11, 11, 11)
                                                    .addGroup(dialogAbilitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(modifierDEX)
                                                        .addComponent(modifierSTR)
                                                        .addComponent(modifierCON)
                                                        .addComponent(modifierINT)
                                                        .addComponent(modifierWIS)
                                                        .addComponent(modifierCHA)))))
                                        .addGroup(dialogAbilitiesLayout.createSequentialGroup()
                                            .addComponent(buttonASC)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(buttonACancel))))))
                        .addComponent(instructionsTwo, javax.swing.GroupLayout.Alignment.LEADING)))
                .addContainerGap(56, Short.MAX_VALUE))
        );
        dialogAbilitiesLayout.setVerticalGroup(
            dialogAbilitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogAbilitiesLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(labelAbilities)
                .addGap(18, 18, 18)
                .addComponent(labelCR)
                .addGap(18, 18, 18)
                .addComponent(labelInstructions)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(instructionsOne)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(instructionsTwo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(instructionsThree)
                .addGap(37, 37, 37)
                .addGroup(dialogAbilitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelPoints)
                    .addComponent(labelRB)
                    .addComponent(labelTotal)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(dialogAbilitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelStrenght)
                    .addComponent(spinnerSTR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bonusSTR)
                    .addComponent(totalSTR)
                    .addComponent(modifierSTR))
                .addGap(18, 18, 18)
                .addGroup(dialogAbilitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelDexterity)
                    .addComponent(spinnerDEX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bonusDEX)
                    .addComponent(totalDEX)
                    .addComponent(modifierDEX))
                .addGap(18, 18, 18)
                .addGroup(dialogAbilitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelConstitution)
                    .addComponent(spinnerCON, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bonusCON)
                    .addComponent(totalCON)
                    .addComponent(modifierCON))
                .addGap(18, 18, 18)
                .addGroup(dialogAbilitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelIntelligence)
                    .addComponent(spinnerINT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bonusINT)
                    .addComponent(totalINT)
                    .addComponent(modifierINT))
                .addGap(18, 18, 18)
                .addGroup(dialogAbilitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelWisdom)
                    .addComponent(spinnerWIS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bonusWIS)
                    .addComponent(totalWIS)
                    .addComponent(modifierWIS))
                .addGap(18, 18, 18)
                .addGroup(dialogAbilitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelCharisma)
                    .addComponent(spinnerCHA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bonusCHA)
                    .addComponent(totalCHA)
                    .addComponent(modifierCHA))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 59, Short.MAX_VALUE)
                .addGroup(dialogAbilitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonASC)
                    .addComponent(buttonACancel))
                .addGap(52, 52, 52))
        );

        dialogSkills.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        dialogSkills.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                dialogSkillsWindowClosing(evt);
            }
        });

        labelSkills.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        labelSkills.setText("Skills");

        labelProficiency.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelProficiency.setText("Proficiency bonus: + 2");

        labelAcro.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelAcro.setText("Acrobatics (Dex): ");

        labelAnim.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelAnim.setText("Animal Handling (Wis):");

        labelArca.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelArca.setText("Arcana (Int):");

        labelAthl.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelAthl.setText("Athletics (Str):");

        labelDece.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelDece.setText("Deception (Cha):");

        labelHist.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelHist.setText("History (Int):");

        labelInsi.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelInsi.setText("Insight (Wis):");

        labelInti.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelInti.setText("Intimidation (Cha):");

        labelInve.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelInve.setText("Investigation (Int):");

        labelMedi.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelMedi.setText("Medicine (Wis):");

        labelNatu.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelNatu.setText("Nature (Int):");

        labelPerc.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelPerc.setText("Perception (Wis):");

        labelPerf.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelPerf.setText("Performance (Cha):");

        labelPers.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelPers.setText("Persuasion (Cha):");

        labelReli.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelReli.setText("Religion (Int):");

        labelSlei.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelSlei.setText("Sleight of Hand (Dex):");

        labelStea.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelStea.setText("Stealth (Dex):");

        labelSurv.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelSurv.setText("Survival (Wis):");

        modifierAcro.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        modifierAcro.setText("     ");
        modifierAcro.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        modifierAnim.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        modifierAnim.setText("     ");
        modifierAnim.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        modifierArca.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        modifierArca.setText("     ");
        modifierArca.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        modifierAthl.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        modifierAthl.setText("     ");
        modifierAthl.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        modifierDece.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        modifierDece.setText("     ");
        modifierDece.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        modifierHist.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        modifierHist.setText("     ");
        modifierHist.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        modifierInsi.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        modifierInsi.setText("     ");
        modifierInsi.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        modifierInti.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        modifierInti.setText("     ");
        modifierInti.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        modifierInve.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        modifierInve.setText("     ");
        modifierInve.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        modifierMedi.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        modifierMedi.setText("     ");
        modifierMedi.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        modifierNatu.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        modifierNatu.setText("     ");
        modifierNatu.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        modifierPerc.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        modifierPerc.setText("     ");
        modifierPerc.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        modifierPerf.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        modifierPerf.setText("     ");
        modifierPerf.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        modifierPers.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        modifierPers.setText("     ");
        modifierPers.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        modifierReli.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        modifierReli.setText("     ");
        modifierReli.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        modifierSlei.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        modifierSlei.setText("     ");
        modifierSlei.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        modifierStea.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        modifierStea.setText("     ");
        modifierStea.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        modifierSurv.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        modifierSurv.setText("     ");
        modifierSurv.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        labelClassProficiencies.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelClassProficiencies.setText("Second, choose and select x class () skill proficienciencies of the skills below:");

        buttonSOk.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        buttonSOk.setText("Ok");
        buttonSOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSOkActionPerformed(evt);
            }
        });

        textPaneSkillsC.setEditable(false);
        textPaneSkillsC.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jScrollPane8.setViewportView(textPaneSkillsC);

        labelSBackg.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelSBackg.setText("First, select the background () skill proficiencies of the skills below:");

        textPaneSkillsB.setEditable(false);
        textPaneSkillsB.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jScrollPane9.setViewportView(textPaneSkillsB);

        checkB1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        checkB1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                checkB1StateChanged(evt);
            }
        });

        checkB2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        checkB2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                checkB2StateChanged(evt);
            }
        });

        checkB3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        checkB3.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                checkB3StateChanged(evt);
            }
        });

        checkB4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        checkB4.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                checkB4StateChanged(evt);
            }
        });

        checkB5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        checkB5.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                checkB5StateChanged(evt);
            }
        });

        checkB6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        checkB6.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                checkB6StateChanged(evt);
            }
        });

        checkB7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        checkB7.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                checkB7StateChanged(evt);
            }
        });

        checkB8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        checkB8.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                checkB8StateChanged(evt);
            }
        });

        checkB9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        checkB9.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                checkB9StateChanged(evt);
            }
        });

        checkB10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        checkB10.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                checkB10StateChanged(evt);
            }
        });

        checkB11.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        checkB11.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                checkB11StateChanged(evt);
            }
        });

        checkB12.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        checkB12.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                checkB12StateChanged(evt);
            }
        });

        checkB13.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        checkB13.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                checkB13StateChanged(evt);
            }
        });

        checkB14.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        checkB14.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                checkB14StateChanged(evt);
            }
        });

        checkB15.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        checkB15.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                checkB15StateChanged(evt);
            }
        });

        checkB16.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        checkB16.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                checkB16StateChanged(evt);
            }
        });

        checkB17.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        checkB17.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                checkB17StateChanged(evt);
            }
        });

        checkB18.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        checkB18.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                checkB18StateChanged(evt);
            }
        });

        javax.swing.GroupLayout dialogSkillsLayout = new javax.swing.GroupLayout(dialogSkills.getContentPane());
        dialogSkills.getContentPane().setLayout(dialogSkillsLayout);
        dialogSkillsLayout.setHorizontalGroup(
            dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogSkillsLayout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelSkills)
                    .addGroup(dialogSkillsLayout.createSequentialGroup()
                        .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(modifierAnim)
                            .addComponent(modifierArca)
                            .addComponent(modifierAthl)
                            .addComponent(modifierDece)
                            .addComponent(modifierAcro)
                            .addComponent(modifierHist)
                            .addComponent(modifierInsi)
                            .addComponent(modifierInti)
                            .addComponent(modifierInve)
                            .addComponent(modifierMedi)
                            .addComponent(modifierNatu)
                            .addComponent(modifierPerc)
                            .addComponent(modifierPerf)
                            .addComponent(modifierPers)
                            .addComponent(modifierReli)
                            .addComponent(modifierSlei)
                            .addComponent(modifierStea)
                            .addComponent(modifierSurv))
                        .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(dialogSkillsLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labelAcro)
                                    .addComponent(labelAnim)
                                    .addComponent(labelArca)
                                    .addComponent(labelAthl)
                                    .addComponent(labelDece)
                                    .addComponent(labelReli)
                                    .addComponent(labelHist)
                                    .addComponent(labelInsi)
                                    .addComponent(labelInti)
                                    .addComponent(labelInve)
                                    .addComponent(labelMedi)
                                    .addComponent(labelNatu)
                                    .addComponent(labelPerc)
                                    .addComponent(labelPerf)
                                    .addComponent(labelSlei)
                                    .addComponent(labelStea)
                                    .addComponent(labelSurv)
                                    .addComponent(labelPers))
                                .addGap(30, 30, 30)
                                .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(checkB18)
                                    .addComponent(checkB17)
                                    .addComponent(checkB16)
                                    .addComponent(checkB13)
                                    .addComponent(checkB12)
                                    .addComponent(checkB11)
                                    .addComponent(checkB10)
                                    .addComponent(checkB9)
                                    .addComponent(checkB8)
                                    .addComponent(checkB7)
                                    .addComponent(checkB6)
                                    .addComponent(checkB2)
                                    .addComponent(checkB1)
                                    .addComponent(checkB3)
                                    .addComponent(checkB4)
                                    .addComponent(checkB5)
                                    .addComponent(checkB15)
                                    .addComponent(checkB14)))
                            .addGroup(dialogSkillsLayout.createSequentialGroup()
                                .addGap(131, 131, 131)
                                .addComponent(labelProficiency)))))
                .addGap(77, 77, 77)
                .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelClassProficiencies)
                    .addComponent(labelSBackg)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(buttonSOk)
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(28, 28, 28))
        );
        dialogSkillsLayout.setVerticalGroup(
            dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dialogSkillsLayout.createSequentialGroup()
                .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dialogSkillsLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(labelSBackg))
                    .addGroup(dialogSkillsLayout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(labelSkills)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(labelProficiency)))
                .addGap(18, 18, 18)
                .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dialogSkillsLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dialogSkillsLayout.createSequentialGroup()
                                .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(labelAcro)
                                    .addComponent(modifierAcro))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(dialogSkillsLayout.createSequentialGroup()
                                .addComponent(checkB1)
                                .addGap(4, 4, 4)))
                        .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(checkB2)
                            .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(labelAnim)
                                .addComponent(modifierAnim)))
                        .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(dialogSkillsLayout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(labelArca)
                                    .addComponent(modifierArca)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dialogSkillsLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(checkB3)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(labelAthl)
                                .addComponent(modifierAthl))
                            .addComponent(checkB4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(labelDece)
                                .addComponent(modifierDece))
                            .addComponent(checkB5)))
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, dialogSkillsLayout.createSequentialGroup()
                        .addComponent(labelClassProficiencies)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, dialogSkillsLayout.createSequentialGroup()
                        .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(modifierHist)
                                .addComponent(labelHist))
                            .addComponent(checkB6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(modifierInsi)
                                .addComponent(labelInsi))
                            .addComponent(checkB7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(modifierInti)
                                .addComponent(labelInti))
                            .addComponent(checkB8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(modifierInve)
                                .addComponent(labelInve))
                            .addComponent(checkB9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(modifierMedi)
                                .addComponent(labelMedi))
                            .addComponent(checkB10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(modifierNatu)
                                .addComponent(labelNatu))
                            .addComponent(checkB11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(modifierPerc)
                                .addComponent(labelPerc))
                            .addComponent(checkB12))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(modifierPerf)
                            .addComponent(labelPerf)
                            .addComponent(checkB13))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(modifierPers)
                                .addComponent(labelPers))
                            .addComponent(checkB14))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(modifierReli)
                            .addComponent(labelReli)
                            .addComponent(checkB15))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(modifierSlei)
                            .addComponent(labelSlei)
                            .addComponent(checkB16))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(modifierStea)
                                .addComponent(labelStea))
                            .addComponent(checkB17))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(dialogSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(modifierSurv)
                                .addComponent(labelSurv))
                            .addComponent(checkB18))))
                .addGap(18, 18, 18)
                .addComponent(buttonSOk)
                .addGap(46, 46, 46))
        );

        dialogStory.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        dialogStory.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                dialogStoryWindowClosing(evt);
            }
        });

        labelDialogStoryHeader.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        labelDialogStoryHeader.setText("Additional background story");

        textAreaStory.setColumns(20);
        textAreaStory.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        textAreaStory.setRows(5);
        jScrollPane7.setViewportView(textAreaStory);

        buttonSaveS.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        buttonSaveS.setText("Save story");
        buttonSaveS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveSActionPerformed(evt);
            }
        });

        buttonCancelS.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        buttonCancelS.setText("Cancel changes");
        buttonCancelS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelSActionPerformed(evt);
            }
        });

        buttonUndo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        buttonUndo.setText("Undo (ctrl + Z)");
        buttonUndo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonUndoActionPerformed(evt);
            }
        });

        buttonRedo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        buttonRedo.setText("Redo (ctrl + Y)");
        buttonRedo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRedoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout dialogStoryLayout = new javax.swing.GroupLayout(dialogStory.getContentPane());
        dialogStory.getContentPane().setLayout(dialogStoryLayout);
        dialogStoryLayout.setHorizontalGroup(
            dialogStoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogStoryLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(dialogStoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dialogStoryLayout.createSequentialGroup()
                        .addGroup(dialogStoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 613, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelDialogStoryHeader))
                        .addContainerGap(24, Short.MAX_VALUE))
                    .addGroup(dialogStoryLayout.createSequentialGroup()
                        .addComponent(buttonUndo)
                        .addGap(18, 18, 18)
                        .addComponent(buttonRedo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(buttonSaveS)
                        .addGap(51, 51, 51)
                        .addComponent(buttonCancelS)
                        .addGap(40, 40, 40))))
        );
        dialogStoryLayout.setVerticalGroup(
            dialogStoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogStoryLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(labelDialogStoryHeader)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 513, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(dialogStoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonSaveS)
                    .addComponent(buttonCancelS)
                    .addComponent(buttonUndo)
                    .addComponent(buttonRedo))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        halfElfInfoOne.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        halfElfInfoOne.setText("Half-elf character's charisma score increases by 2, and they get two");

        halfElfInfoTwo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        halfElfInfoTwo.setText("other ability scores of the player's choice increased by 1.");

        halfElfInfoThree.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        halfElfInfoThree.setText("Choose two abilities:");

        halfElfSTR.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        halfElfSTR.setText("Strength");
        halfElfSTR.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                halfElfSTRItemStateChanged(evt);
            }
        });

        halfElfDEX.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        halfElfDEX.setText("Dexterity");
        halfElfDEX.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                halfElfDEXItemStateChanged(evt);
            }
        });

        halfElfCON.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        halfElfCON.setText("Constitution");
        halfElfCON.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                halfElfCONStateChanged(evt);
            }
        });

        halfElfINT.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        halfElfINT.setText("Intelligence");
        halfElfINT.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                halfElfINTStateChanged(evt);
            }
        });

        halfElfWIS.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        halfElfWIS.setText("Wisdom");
        halfElfWIS.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                halfElfWISStateChanged(evt);
            }
        });

        buttonSaveClose.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        buttonSaveClose.setText("Save and close");
        buttonSaveClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveCloseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout halfElfStatsLayout = new javax.swing.GroupLayout(halfElfStats.getContentPane());
        halfElfStats.getContentPane().setLayout(halfElfStatsLayout);
        halfElfStatsLayout.setHorizontalGroup(
            halfElfStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(halfElfStatsLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(halfElfStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(buttonSaveClose)
                    .addGroup(halfElfStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(halfElfCON)
                        .addComponent(halfElfInfoOne)
                        .addComponent(halfElfInfoTwo)
                        .addComponent(halfElfInfoThree)
                        .addGroup(halfElfStatsLayout.createSequentialGroup()
                            .addGroup(halfElfStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(halfElfSTR)
                                .addComponent(halfElfDEX))
                            .addGap(104, 104, 104)
                            .addGroup(halfElfStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(halfElfWIS)
                                .addComponent(halfElfINT)))))
                .addContainerGap(109, Short.MAX_VALUE))
        );
        halfElfStatsLayout.setVerticalGroup(
            halfElfStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(halfElfStatsLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(halfElfInfoOne)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(halfElfInfoTwo)
                .addGap(18, 18, 18)
                .addComponent(halfElfInfoThree)
                .addGap(18, 18, 18)
                .addGroup(halfElfStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(halfElfSTR)
                    .addComponent(halfElfINT))
                .addGap(18, 18, 18)
                .addGroup(halfElfStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(halfElfDEX)
                    .addComponent(halfElfWIS))
                .addGap(18, 18, 18)
                .addComponent(halfElfCON)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonSaveClose)
                .addContainerGap(34, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        header.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        header.setText("D&D Character Creator");

        labelName.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelName.setText("Name:");

        textFieldName.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        labelRace.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelRace.setText("Race:");

        labelClass.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelClass.setText("Class:");

        comboBoxClass.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        comboBoxClass.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Barbarian", "Bard", "Cleric", "Druid", "Fighter", "Monk", "Paladin", "Ranger", "Rogue", "Sorcerer", "Warlock", "Wizard" }));

        labelBackground.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelBackground.setText("Background:");

        comboBoxBackground.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        comboBoxBackground.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Acolyte", "Charlatan", "Criminal", "Entertainer", "Folk Hero", "Guild Artisan", "Hermit", "Noble", "Outlander", "Sage", "Sailor", "Soldier", "Urchin" }));

        labelBasicI.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelBasicI.setText("Basic Information:");

        tableBasicI.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tableBasicI.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Age", ""},
                {"Sex", ""},
                {"Height", ""},
                {"Weight", ""},
                {"Eyes", ""},
                {"Skin", ""},
                {"Hair", ""}
            },
            new String [] {
                "Features", "Your character"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableBasicI.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tableBasicI);
        if (tableBasicI.getColumnModel().getColumnCount() > 0) {
            tableBasicI.getColumnModel().getColumn(0).setResizable(false);
            tableBasicI.getColumnModel().getColumn(1).setResizable(false);
        }

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Alignment:");

        comboBoxAlignment.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        comboBoxAlignment.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Lawful good (LG)", "Neutral good (NG)", "Chaotic good (CG)", "Lawful neutral (LN)", "Neutral (N)", "Chaotic neutral (CN)", "Lawful evil (LE)", "Neutral evil (NE)", "Chaotic evil (CE)" }));

        buttonAbilities.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        buttonAbilities.setText("Abilities");
        buttonAbilities.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAbilitiesActionPerformed(evt);
            }
        });

        buttonSkills.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        buttonSkills.setText("Skills");
        buttonSkills.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSkillsActionPerformed(evt);
            }
        });

        abilityPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        labelSTR.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelSTR.setText("STR: ");

        labelDEX.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelDEX.setText("DEX: ");

        labelCON.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelCON.setText("CON: ");

        labelINT.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelINT.setText("INT: ");

        labelWIS.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelWIS.setText("WIS: ");

        labelCHA.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelCHA.setText("CHA: ");

        javax.swing.GroupLayout abilityPanelLayout = new javax.swing.GroupLayout(abilityPanel);
        abilityPanel.setLayout(abilityPanelLayout);
        abilityPanelLayout.setHorizontalGroup(
            abilityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(abilityPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(abilityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelSTR)
                    .addComponent(labelDEX)
                    .addComponent(labelCON))
                .addGap(32, 32, 32)
                .addGroup(abilityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelCHA)
                    .addComponent(labelWIS)
                    .addComponent(labelINT))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        abilityPanelLayout.setVerticalGroup(
            abilityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(abilityPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(abilityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelSTR)
                    .addComponent(labelINT))
                .addGap(18, 18, 18)
                .addGroup(abilityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelDEX)
                    .addComponent(labelWIS))
                .addGap(18, 18, 18)
                .addGroup(abilityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelCON)
                    .addComponent(labelCHA))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        buttonPicture.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        buttonPicture.setText("Add a picture");
        buttonPicture.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPictureActionPerformed(evt);
            }
        });

        labelPersonality.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelPersonality.setText("Personality traits:");

        textAPersonality.setColumns(20);
        textAPersonality.setRows(5);
        jScrollPane2.setViewportView(textAPersonality);

        labelIdeals.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelIdeals.setText("Ideals:");

        textAIdeals.setColumns(20);
        textAIdeals.setRows(5);
        jScrollPane3.setViewportView(textAIdeals);

        labelBonds.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelBonds.setText("Bonds:");

        textABonds.setColumns(20);
        textABonds.setRows(5);
        jScrollPane4.setViewportView(textABonds);

        labelFlaws.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelFlaws.setText("Flaws:");

        textAFlaws.setColumns(20);
        textAFlaws.setRows(5);
        jScrollPane5.setViewportView(textAFlaws);

        labelFeatsTraits.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelFeatsTraits.setText("Features & traits:");

        textAFeatsTraits.setColumns(20);
        textAFeatsTraits.setRows(5);
        jScrollPane6.setViewportView(textAFeatsTraits);

        buttonStory.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        buttonStory.setText("Additional background story");
        buttonStory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStoryActionPerformed(evt);
            }
        });

        buttonSave.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        buttonSave.setText("Save");
        buttonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveActionPerformed(evt);
            }
        });

        buttonCancel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        buttonCancel.setText("Cancel");
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        comboBoxRace.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        comboBoxRace.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Dragonborn", "Dwarf", "Elf", "Gnome", "Half-Elf", "Halfling", "Half-Orc", "Human", "Tiefling", " " }));

        picture1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout picture1Layout = new javax.swing.GroupLayout(picture1);
        picture1.setLayout(picture1Layout);
        picture1Layout.setHorizontalGroup(
            picture1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        picture1Layout.setVerticalGroup(
            picture1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        buttonSaveAs.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        buttonSaveAs.setText("Save As...");
        buttonSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveAsActionPerformed(evt);
            }
        });

        menuFile.setText("File");

        menuNewC.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        menuNewC.setText("New Character");
        menuNewC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuNewCActionPerformed(evt);
            }
        });
        menuFile.add(menuNewC);

        menuLoadC.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        menuLoadC.setText("Load Character...");
        menuLoadC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuLoadCActionPerformed(evt);
            }
        });
        menuFile.add(menuLoadC);

        menuSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        menuSave.setText("Save");
        menuSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSaveActionPerformed(evt);
            }
        });
        menuFile.add(menuSave);

        menuSaveAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        menuSaveAs.setText("Save As...");
        menuSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSaveAsActionPerformed(evt);
            }
        });
        menuFile.add(menuSaveAs);

        menuExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
        menuExit.setText("Exit");
        menuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuExitActionPerformed(evt);
            }
        });
        menuFile.add(menuExit);

        jMenuBar1.add(menuFile);

        disableEditSettings.setText("Settings");

        menuFont.setText("Font Size");

        fontSButtonGroup.add(font12);
        font12.setSelected(true);
        font12.setText("12");
        font12.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                font12StateChanged(evt);
            }
        });
        menuFont.add(font12);

        fontSButtonGroup.add(font14);
        font14.setText("14");
        font14.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                font14StateChanged(evt);
            }
        });
        menuFont.add(font14);

        fontSButtonGroup.add(font16);
        font16.setText("16");
        font16.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                font16StateChanged(evt);
            }
        });
        menuFont.add(font16);

        disableEditSettings.add(menuFont);

        menuDisableE.setText("Disable Editing");
        menuDisableE.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                menuDisableEStateChanged(evt);
            }
        });
        disableEditSettings.add(menuDisableE);

        menuResetC.setText("Reset Character");
        menuResetC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuResetCActionPerformed(evt);
            }
        });
        disableEditSettings.add(menuResetC);

        jMenuBar1.add(disableEditSettings);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelFlaws)
                        .addGap(281, 281, 281)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 563, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelFeatsTraits))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jScrollPane2)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(314, 314, 314)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(labelBonds)
                                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(comboBoxAlignment, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(labelBackground, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labelClass, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labelRace, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(textFieldName, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labelName, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(header, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(comboBoxClass, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(comboBoxBackground, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(comboBoxRace, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(42, 42, 42)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(labelBasicI)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(buttonSkills, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(buttonAbilities, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGap(18, 18, 18)
                                        .addComponent(abilityPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                            .addComponent(jLabel2)
                            .addComponent(labelPersonality)
                            .addComponent(labelIdeals)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 62, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(picture1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(buttonStory)
                                .addGap(109, 109, 109)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(buttonSaveAs)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(buttonSave, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(buttonPicture, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(57, 57, 57))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(47, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(header)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(labelName)
                                    .addComponent(labelBasicI))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(textFieldName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(13, 13, 13)
                                        .addComponent(labelRace)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(comboBoxRace, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(labelClass)
                                        .addGap(18, 18, 18)
                                        .addComponent(comboBoxClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(labelBackground)
                                            .addComponent(buttonAbilities))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(comboBoxBackground, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(buttonSkills))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(comboBoxAlignment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(abilityPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(34, 34, 34)
                                .addComponent(labelPersonality)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(labelIdeals)
                                    .addComponent(labelBonds))
                                .addGap(20, 20, 20)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                                    .addComponent(jScrollPane4)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(picture1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(buttonPicture)
                                    .addComponent(buttonStory))))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelFlaws)
                            .addComponent(labelFeatsTraits))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(buttonSaveAs)
                        .addGap(29, 29, 29)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(buttonSave)
                            .addComponent(buttonCancel))))
                .addGap(46, 46, 46))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonAbilitiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAbilitiesActionPerformed
        /* The Ability dialog window opens for viewing, the race of the character
         * is checked for getting the right race bonus ability points.
         */
        raceName = comboBoxRace.getSelectedItem().toString();
        labelCR.setText("Character's race: " + raceName);
        updateRaceBonuses();
        dialogAbilities.setVisible(true);
        
        /* If the character is not loaded from a file, the method for counting
         * ability and modifier points is called.
         */
       
        if (oldCharacterFirstView == false) {
            countTotalAbilityAndModifierScore();
        }
        /* Else, another method is used, so that the values of spinners do not
         * accidentally reset.
         */
        else {
            updateSpinnersAndRaceBonuses();
        }
        /* If race = half-elf, additional popup-menu opens, and user chooses
         * two abilities with which he gets now +1 race bonus point. This feature
         * is unique and only appears, if the character is an half-elf.
         */
        if (raceName.equals("Half-Elf")) {
            // If Disable Editing setting is not activated, the popup-menu appears
            if (menuDisableE.isSelected() == false) {
                    halfElfStats.setVisible(true);
            }    
        }
        /* It's possible that the user changes some of the ability points and
         * then uses Skills window again or saves before viewing skills again.
         * For that reason it's good to take copies of the current modifier points,
         * so that they can be compared later with the possible new point values.
         */
        copyOfSTR = modifierSTR.getText();
        copyOfDEX = modifierDEX.getText();
        copyOfINT = modifierINT.getText();
        copyOfWIS = modifierWIS.getText();
        copyOfCHA = modifierCHA.getText();
        
        
        /* Copies of the spinner values in case of user cancelling or exiting the
         * Abilities window without saving changes
         */
        copySpinnerValues();
    }//GEN-LAST:event_buttonAbilitiesActionPerformed

    private void buttonACancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonACancelActionPerformed
        // If the user uses Cancel button in Ability window
        spinnerSTR.setValue(copyStr);
        spinnerDEX.setValue(copyDex);
        spinnerCON.setValue(copyCon);
        spinnerINT.setValue(copyInt);
        spinnerWIS.setValue(copyWis);
        spinnerCHA.setValue(copyCha);
        
        // Taking a new copy of the values again
        copySpinnerValues();
    }//GEN-LAST:event_buttonACancelActionPerformed

    private void dialogAbilitiesWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_dialogAbilitiesWindowClosing
        // When the user tries to close the Ability window  
        abilitiesConfirmExit();
    }//GEN-LAST:event_dialogAbilitiesWindowClosing

    private void buttonStoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStoryActionPerformed
        // The additional background story window opens for viewing
        dialogStory.setVisible(true);
        /* The copy of the text is created, in case of user cancelling the changes
         * made
         */
        copyOfStory = textAreaStory.getText();      
    }//GEN-LAST:event_buttonStoryActionPerformed

    private void buttonCancelSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelSActionPerformed
        /* When the user uses Cancel button in the Story window, the last version
         * of the story is returned (the empthy text area or the last saved version)
         */
        textAreaStory.setText(copyOfStory);
        copyOfStory = textAreaStory.getText();              
    }//GEN-LAST:event_buttonCancelSActionPerformed

    private void dialogStoryWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_dialogStoryWindowClosing
        // User tries to close the Story window
        storyConfirmExit();
    }//GEN-LAST:event_dialogStoryWindowClosing

    private void buttonSkillsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSkillsActionPerformed
        /* If the Skills window opens for viewing for the first time, the skill
         * points are counted at first without proficiencies, and all the modifiers
         * values are copied.
         */
        dialogSkills.setVisible(true);
        
        if (firstSkillView == true) {
            countSkillPoints();
            className = comboBoxClass.getSelectedItem().toString();
            // The copy values are set here for the first time, they are updated later
            copyClassN = className;
            copyBackground = comboBoxBackground.getSelectedItem().toString();
            /* The copies of modifier points are updated also here.
             */
            copyOfSTR = modifierSTR.getText();
            copyOfDEX = modifierDEX.getText();
            copyOfINT = modifierINT.getText();
            copyOfWIS = modifierWIS.getText();
            copyOfCHA = modifierCHA.getText();
            
            /* It's not necessary to count all the points after closing and
             * opening the skill window again (as the proficiency points would
             * also get lost if countSkillPoints method is used again), so this
             * boolean's value is changed.
             */
            firstSkillView = false;
        }
        
        // If some of the ability modifiers has changed after the first view of Skills
        if ((!copyOfSTR.equals(modifierSTR.getText())) || (!copyOfDEX.equals(modifierDEX.getText())) ||
        (!copyOfINT.equals(modifierINT.getText())) || (!copyOfWIS.equals(modifierWIS.getText())) ||
        (!copyOfCHA.equals(modifierCHA.getText()))) {
            updateSkillPoints();
        }
        
        // When user loads character from a file
        if (oldCSkills == true) {
            // The user is asked to select the skill proficiencis again
            countSkillPoints();     
            oldCSkills = false;
        }

        className = comboBoxClass.getSelectedItem().toString();
        background = comboBoxBackground.getSelectedItem().toString();
            
        CharacterStats charSkills = new CharacterStats();
        
        /* Setting the available background proficiency skills (always two of them).
         * The user is instructed to pick the two skills of listed into the text
         * area below. The selection is made with checkboxes.
         */
        labelSBackg.setText("First, select the background (" + background + 
        ") skill proficiencies of the skills below:");
        charSkills.setBackgroundSkills(background);
        textPaneSkillsB.setText((charSkills.getBackgroundSkills(0)) +  '\n' + 
                (charSkills.getBackgroundSkills(1)));
        
        // Gets the amount of class proficiency skill points that the character has
        charSkills.setAmountOfClassSkills(className);
        int amountOfSkills = charSkills.getAmountOfClassSkills();
        /* Setting the available class proficiency skills similar to background,
         * but now the user can only choose as many skills as the amounOfSkills
         * defines. The choices are made with checkboxes.
        */
        labelClassProficiencies.setText("Second, choose and select " + 
                amountOfSkills + " class (" + className +
                ") skill proficiencies of the skills below:");
        charSkills.setClassSkills(className);
        
        /* Listing the available class skills. Bard class can choose proficiencies
         * from any of the 18 different skills, so therefore 18 lines are always
         * printed, but the content varies by the class chosen.
         */
        textPaneSkillsC.setText((charSkills.getClassSkills(0))+ '\n' +
                (charSkills.getClassSkills(1))+ '\n' +
                (charSkills.getClassSkills(2))+ '\n' +
                (charSkills.getClassSkills(3)) + '\n' +
                (charSkills.getClassSkills(4)) + '\n' +
                (charSkills.getClassSkills(5)) + '\n' +
                (charSkills.getClassSkills(6)) + '\n' +
                (charSkills.getClassSkills(7)) + '\n' +
                (charSkills.getClassSkills(8)) + '\n' +
                (charSkills.getClassSkills(9)) + '\n' +
                (charSkills.getClassSkills(10)) + '\n' +
                (charSkills.getClassSkills(11)) + '\n' +
                (charSkills.getClassSkills(12)) + '\n' +
                (charSkills.getClassSkills(13)) + '\n' +
                (charSkills.getClassSkills(14)) + '\n' +
                (charSkills.getClassSkills(15)) + '\n' +
                (charSkills.getClassSkills(16)) + '\n' +
                (charSkills.getClassSkills(17)));
    }//GEN-LAST:event_buttonSkillsActionPerformed

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        // User uses Cancel button in the main view of the program
        confirmCancelOrExit();
    }//GEN-LAST:event_buttonCancelActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // Window closing
        confirmCancelOrExit();
    }//GEN-LAST:event_formWindowClosing

    private void menuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuExitActionPerformed
        // Window closing (from the File menu)
        confirmCancelOrExit();
    }//GEN-LAST:event_menuExitActionPerformed

    private void halfElfSTRItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_halfElfSTRItemStateChanged
        // Half-elf, choosing to use race bonus point for STR
        if (halfElfSTR.isSelected())
            halfElfStatsUpdate();
    }//GEN-LAST:event_halfElfSTRItemStateChanged

    private void halfElfDEXItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_halfElfDEXItemStateChanged
        // Half-elf, choosing to use race bonus point for DEX
        if (halfElfDEX.isSelected())
            halfElfStatsUpdate();
    }//GEN-LAST:event_halfElfDEXItemStateChanged

    private void halfElfCONStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_halfElfCONStateChanged
        // Half-elf, choosing to use race bonus point for CON
        if (halfElfCON.isSelected())
            halfElfStatsUpdate();
    }//GEN-LAST:event_halfElfCONStateChanged

    private void halfElfINTStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_halfElfINTStateChanged
        // Half-elf, choosing to use race bonus point for INT
        if (halfElfINT.isSelected())
            halfElfStatsUpdate();
    }//GEN-LAST:event_halfElfINTStateChanged

    private void halfElfWISStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_halfElfWISStateChanged
        // Half-elf, choosing to use race bonus point for WIS
        if (halfElfWIS.isSelected())
            halfElfStatsUpdate();
    }//GEN-LAST:event_halfElfWISStateChanged

    private void buttonSaveCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveCloseActionPerformed
        /* When half-elf character's additional two bonus points for ability scores
         * are saved, they are updated to the race bonus point scores.
         */             
        if (halfElfSTR.isSelected()) {
            bonusSTR.setText("1");
        }
        if (halfElfDEX.isSelected()) {
            bonusDEX.setText("1");
        }
        if (halfElfCON.isSelected()) {
            bonusCON.setText("1");
        }
        if (halfElfINT.isSelected()) {
            bonusINT.setText("1");
        }
        if (halfElfWIS.isSelected()) {
            bonusWIS.setText("1");
        }
        // Total ability score and modifier points also needs to be updated.
        countTotalAbilityAndModifierScore();
        halfElfStats.dispose();
            
    }//GEN-LAST:event_buttonSaveCloseActionPerformed

    private void spinnerSTRStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerSTRStateChanged
        /* When spinnerSTR state changes, ability score and modifier score must 
         * be counted. Viewing an old character is taken into consideration.
         */
        if (oldCharacterFirstView == false) {
            countTotalAbilityAndModifierScore();
        }      
    }//GEN-LAST:event_spinnerSTRStateChanged

    private void spinnerDEXStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerDEXStateChanged
        /* When spinnerDEX state changes, ability score and modifier score must 
         * be counted. Viewing an old character is taken into consideration.
         */
        if (oldCharacterFirstView == false) {
            countTotalAbilityAndModifierScore();
        }
    }//GEN-LAST:event_spinnerDEXStateChanged

    private void spinnerCONStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerCONStateChanged
        /* When spinnerCON state changes, ability score and modifier score must 
         * be counted. Viewing an old character is taken into consideration.
         */
        if (oldCharacterFirstView == false) {
            countTotalAbilityAndModifierScore();
        }
    }//GEN-LAST:event_spinnerCONStateChanged

    private void spinnerINTStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerINTStateChanged
        /* When spinnerINT state changes, ability score and modifier score must 
         * be counted. Viewing an old character is taken into consideration.
         */
        if (oldCharacterFirstView == false) {
            countTotalAbilityAndModifierScore();
        }
    }//GEN-LAST:event_spinnerINTStateChanged

    private void spinnerWISStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerWISStateChanged
        /* When spinnerWIS state changes, ability score and modifier score must 
         * be counted. Viewing an old character is taken into consideration.
         */
        if (oldCharacterFirstView == false) {
            countTotalAbilityAndModifierScore();
        }
    }//GEN-LAST:event_spinnerWISStateChanged

    private void spinnerCHAStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerCHAStateChanged
        /* When spinnerCHA state changes, ability score and modifier score must 
         * be counted. Viewing an old character is taken into consideration.
         */
        if (oldCharacterFirstView == false) {
            countTotalAbilityAndModifierScore();
        }
    }//GEN-LAST:event_spinnerCHAStateChanged

    private void buttonASCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonASCActionPerformed
        // Ability points are saved and the window closes
        printAbilityModifierPoints();
        copySpinnerValues();
        /* In case of that the ability points have been changed, but skills are
         * not viewed after that, skill modifier values are updated
         */
        updateSkillPoints();
        dialogAbilities.setVisible(false);
    }//GEN-LAST:event_buttonASCActionPerformed

    private void buttonPictureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPictureActionPerformed
        // User can choose a file for adding a picture of his character.
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            Image i;
            try {
                i = ImageIO.read(chooser.getSelectedFile());
                picture1.setImage(i);
                picture1.repaint();
            } catch (IOException ex) {
                Logger.getLogger(DnDCharacterCreator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_buttonPictureActionPerformed

    private void menuResetCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuResetCActionPerformed
        /* If user is activating Reset from Settings menu, the UI asks of 
         * confirmation of resetting all the information in Character Creator.
         * If approved, all the input data in UI is reset to default.
         */
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to reset"
            + " all the character's features to the default?", "Do you want to reset all"
            + " the character's features?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            
            // Resetting the name, race, class, background and alignment values
            textFieldName.setText("");
            comboBoxRace.setSelectedIndex(0);
            comboBoxClass.setSelectedIndex(0);
            comboBoxBackground.setSelectedIndex(0);
            comboBoxAlignment.setSelectedIndex(0);
            
            // Resetting the table values of basic information
            tableBasicI.getModel().setValueAt("", 0, 1);
            tableBasicI.getModel().setValueAt("", 1, 1);
            tableBasicI.getModel().setValueAt("", 2, 1);
            tableBasicI.getModel().setValueAt("", 3, 1);
            tableBasicI.getModel().setValueAt("", 4, 1);
            tableBasicI.getModel().setValueAt("", 5, 1);
            tableBasicI.getModel().setValueAt("", 6, 1);
            
            /* Resetting the personality traits, ideals, bonds, flaws, and features
             * & traits.
             */ 
            textAPersonality.setText("");
            textAIdeals.setText("");
            textABonds.setText("");
            textAFlaws.setText("");
            textAFeatsTraits.setText("");
            
            // Resetting the additional background story
            textAreaStory.setText("");
            
            // Resetting the ability scoring and printing the score
            spinnerSTR.setValue(0);
            spinnerDEX.setValue(0);
            spinnerCON.setValue(0);
            spinnerINT.setValue(0);
            spinnerWIS.setValue(0);
            spinnerCHA.setValue(0);
            labelSTR.setText("STR: ");
            labelDEX.setText("DEX: ");
            labelCON.setText("CON: ");
            labelINT.setText("INT: ");
            labelWIS.setText("WIS: ");
            labelCHA.setText("CHA: ");
            
            // Resetting the skill scoring
            modifierAcro.setText("");
            modifierAnim.setText("");
            modifierArca.setText("");
            modifierAthl.setText("");
            modifierDece.setText("");
            modifierHist.setText("");
            modifierInsi.setText("");
            modifierInti.setText("");
            modifierInve.setText("");
            modifierMedi.setText("");
            modifierNatu.setText("");
            modifierPerc.setText("");
            modifierPerf.setText("");
            modifierPers.setText("");
            modifierReli.setText("");
            modifierSlei.setText("");
            modifierStea.setText("");
            modifierSurv.setText("");
            
            // Resetting proficiencies and proficiency checkboxes
            proficiencies = "";
            checkB1.setSelected(false);
            checkB2.setSelected(false);
            checkB3.setSelected(false);
            checkB4.setSelected(false);
            checkB5.setSelected(false);
            checkB6.setSelected(false);
            checkB7.setSelected(false);
            checkB8.setSelected(false);
            checkB9.setSelected(false);
            checkB10.setSelected(false);
            checkB11.setSelected(false);
            checkB12.setSelected(false);
            checkB13.setSelected(false);
            checkB14.setSelected(false);
            checkB15.setSelected(false);
            checkB16.setSelected(false);
            checkB17.setSelected(false);
            checkB18.setSelected(false);
            
            // Removing image from the UI view
            picture1.setImage(null);
            picture1.repaint();  
            
            // The resetted character needs to be saved as a new file
            buttonSave.setEnabled(false);
            menuSave.setEnabled(false);
        }     
    }//GEN-LAST:event_menuResetCActionPerformed

    private void menuDisableEStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_menuDisableEStateChanged
        /* If user clicks Disable editing from the Setting menu, editing of any
         * character information is disabled.
         */
        if (menuDisableE.isSelected()) {
            /* Disable editing of the name, race, class, background and 
             * alignment values
             */
            textFieldName.setEditable(false);
            comboBoxRace.setEnabled(false);
            comboBoxClass.setEnabled(false);
            comboBoxBackground.setEnabled(false);
            comboBoxAlignment.setEnabled(false);
            
            // Disable editing of the table values of basic information
            tableBasicI.setEnabled(false);
            
            /* Disable editing of the personality traits, ideals, bonds, flaws, 
             * and features & traits.
             */ 
            textAPersonality.setEditable(false);
            textAIdeals.setEditable(false);
            textABonds.setEditable(false);
            textAFlaws.setEditable(false);
            textAFeatsTraits.setEditable(false);
            
            // Disable editing of the additional background story
            textAreaStory.setEditable(false);
            
            // Disable editing the ability scoring and printing the score
            spinnerSTR.setEnabled(false);
            spinnerDEX.setEnabled(false);
            spinnerCON.setEnabled(false);
            spinnerINT.setEnabled(false);
            spinnerWIS.setEnabled(false);
            spinnerCHA.setEnabled(false);
            
            // Disable editing the skill proficiencies (aka using checkboxes)
            checkB1.setEnabled(false);
            checkB2.setEnabled(false);
            checkB3.setEnabled(false);
            checkB4.setEnabled(false);
            checkB5.setEnabled(false);
            checkB6.setEnabled(false);
            checkB7.setEnabled(false);
            checkB8.setEnabled(false);
            checkB9.setEnabled(false);
            checkB10.setEnabled(false);
            checkB11.setEnabled(false);
            checkB12.setEnabled(false);
            checkB13.setEnabled(false);
            checkB14.setEnabled(false);
            checkB15.setEnabled(false);
            checkB16.setEnabled(false);
            checkB17.setEnabled(false);
            checkB18.setEnabled(false);
            
            /* Disable various buttons in UI, Reset in Settings menu and Save
             * functions in File menu
             */
            buttonPicture.setEnabled(false);
            buttonSave.setEnabled(false);
            buttonSaveAs.setEnabled(false);
            buttonASC.setEnabled(false);
            buttonACancel.setEnabled(false);
            buttonUndo.setEnabled(false);
            buttonRedo.setEnabled(false);
            buttonSaveS.setEnabled(false);
            buttonCancelS.setEnabled(false);
            menuResetC.setEnabled(false);
            menuSave.setEnabled(false);
            menuSaveAs.setEnabled(false);
                    
        }
        else {
            /* If the Disable editing check box is not selected, everything is
             * again editable. The Save function in the main view is not usable
             * though, and the user should save the character as a new file, 
             * or load character the old character again if he wishes to save
             * changes to the file already existing.
             */           
            textFieldName.setEditable(true);
            comboBoxRace.setEnabled(true);
            comboBoxClass.setEnabled(true);
            comboBoxBackground.setEnabled(true);
            comboBoxAlignment.setEnabled(true);
            tableBasicI.setEnabled(true);
            textAPersonality.setEditable(true);
            textAIdeals.setEditable(true);
            textABonds.setEditable(true);
            textAFlaws.setEditable(true);
            textAFeatsTraits.setEditable(true);
            textAreaStory.setEditable(true);
            spinnerSTR.setEnabled(true);
            spinnerDEX.setEnabled(true);
            spinnerCON.setEnabled(true);
            spinnerINT.setEnabled(true);
            spinnerWIS.setEnabled(true);
            spinnerCHA.setEnabled(true);
            
            checkB1.setEnabled(true);
            checkB2.setEnabled(true);
            checkB3.setEnabled(true);
            checkB4.setEnabled(true);
            checkB5.setEnabled(true);
            checkB6.setEnabled(true);
            checkB7.setEnabled(true);
            checkB8.setEnabled(true);
            checkB9.setEnabled(true);
            checkB10.setEnabled(true);
            checkB11.setEnabled(true);
            checkB12.setEnabled(true);
            checkB13.setEnabled(true);
            checkB14.setEnabled(true);
            checkB15.setEnabled(true);
            checkB16.setEnabled(true);
            checkB17.setEnabled(true);
            checkB18.setEnabled(true);
            
            buttonPicture.setEnabled(true);
            buttonSaveAs.setEnabled(true);     
            buttonASC.setEnabled(true);
            buttonACancel.setEnabled(true);
            buttonUndo.setEnabled(true);
            buttonRedo.setEnabled(true);
            buttonSaveS.setEnabled(true);
            buttonCancelS.setEnabled(true);
            menuResetC.setEnabled(true);
            menuSaveAs.setEnabled(true);
        }
    }//GEN-LAST:event_menuDisableEStateChanged

    private void menuSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSaveActionPerformed
        // Overwriting an existing saved file, saveAs value changing
        saveAs = false;
        saveCharacter();
    }//GEN-LAST:event_menuSaveActionPerformed

    private void menuSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSaveAsActionPerformed
        // Saving character as a txt file, user giving the file name
        saveAs = true;
        saveCharacter();
    }//GEN-LAST:event_menuSaveAsActionPerformed

    private void buttonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveActionPerformed
        // Saving character as a txt file, user giving the file name
        saveAs = false;
        saveCharacter();
    }//GEN-LAST:event_buttonSaveActionPerformed

    private void buttonSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveAsActionPerformed
        // Saving character as a txt file, user giving the file name
        saveAs = true;
        saveCharacter();
    }//GEN-LAST:event_buttonSaveAsActionPerformed

    private void menuLoadCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuLoadCActionPerformed
        // Loading a character's information into the UI from a txt file
        loadCharacter();
    }//GEN-LAST:event_menuLoadCActionPerformed

    private void dialogSkillsWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_dialogSkillsWindowClosing
        // User tries to close the Skills window
        skillsConfirmExit();
    }//GEN-LAST:event_dialogSkillsWindowClosing

    private void buttonSOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSOkActionPerformed
        // Ok button closes the Skills window, and the list of proficiencies is updated.
        listProficiencies();
        dialogSkills.setVisible(false);
    }//GEN-LAST:event_buttonSOkActionPerformed

    private void checkB1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_checkB1StateChanged
        // Acrobatics proficiency checkbox selected/unselected in Skills window
        if (checkB1.isSelected()) {
            prof[0] = "Acrobatics   ";
            modDex = Integer.parseInt(modifierDEX.getText());
            modifierAcro.setText(Integer.toString(modDex + 2));
        } else {
            modifierAcro.setText(modifierDEX.getText());
            prof[0] = d;
        }
        
    }//GEN-LAST:event_checkB1StateChanged

    private void checkB2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_checkB2StateChanged
        // Animal Handling proficiency checkbox selected/unselected in Skills window
        if (checkB2.isSelected()) {
            prof[1] = "Animal Handling   ";
            modWis = Integer.parseInt(modifierWIS.getText());
            modifierAnim.setText(Integer.toString(modWis + 2)); 
        }
        else {
            modifierAnim.setText(modifierWIS.getText());
            prof[1] = d;
        }
    }//GEN-LAST:event_checkB2StateChanged

    private void checkB3StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_checkB3StateChanged
        // Arcana proficiency checkbox selected/unselected in Skills window
        if (checkB3.isSelected()) {
            prof[2] = "Arcana   ";
            modInt = Integer.parseInt(modifierINT.getText());
            modifierArca.setText(Integer.toString(modInt + 2));  
        }
        else {
            modifierArca.setText(modifierINT.getText());
            prof[2] = d;
        }
    }//GEN-LAST:event_checkB3StateChanged

    private void checkB4StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_checkB4StateChanged
        // Athletics proficiency checkbox selected/unselected in Skills window
        if (checkB4.isSelected()) {
            prof[3] = "Athletics   ";
            modStr = Integer.parseInt(modifierSTR.getText());
            modifierAthl.setText(Integer.toString(modStr + 2)); 
        }
        else {
            modifierAthl.setText(modifierSTR.getText());
            prof[3] = d;
        }
    }//GEN-LAST:event_checkB4StateChanged

    private void checkB5StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_checkB5StateChanged
        // Deception proficiency checkbox selected/unselected in Skills window
        if (checkB5.isSelected()) {
            prof[4] = "Deception   ";
            modCha = Integer.parseInt(modifierCHA.getText());
            modifierDece.setText(Integer.toString(modCha + 2)); 
        }
        else {
            modifierDece.setText(modifierCHA.getText());
            prof[4] = d;
        }
    }//GEN-LAST:event_checkB5StateChanged

    private void checkB6StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_checkB6StateChanged
        // History proficiency checkbox selected/unselected in Skills window
        if (checkB6.isSelected()) {
            prof[5] = "History   ";
            modInt = Integer.parseInt(modifierINT.getText());
            modifierHist.setText(Integer.toString(modInt + 2)); 
        }
        else {
            modifierHist.setText(modifierINT.getText());
            prof[5] = d;
        }
    }//GEN-LAST:event_checkB6StateChanged

    private void checkB7StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_checkB7StateChanged
        // Insight proficiency checkbox selected/unselected in Skills window
        if (checkB7.isSelected()) {
            prof[6] = "Insight   ";
            modWis = Integer.parseInt(modifierWIS.getText());
            modifierInsi.setText(Integer.toString(modWis + 2));  
        }
        else {
            modifierInsi.setText(modifierWIS.getText());
            prof[6] = d;
        }
    }//GEN-LAST:event_checkB7StateChanged

    private void checkB8StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_checkB8StateChanged
        // Intimidation proficiency checkbox selected/unselected in Skills window
        if (checkB8.isSelected()) {
            prof[7] = "Intimidation   ";
            modCha = Integer.parseInt(modifierCHA.getText());
            modifierInti.setText(Integer.toString(modCha + 2)); 
        }
        else {
            modifierInti.setText(modifierCHA.getText());
            prof[7] = d;
        }
    }//GEN-LAST:event_checkB8StateChanged

    private void checkB9StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_checkB9StateChanged
        // Investigation proficiency checkbox selected/unselected in Skills window
        if (checkB9.isSelected()) {
            prof[8] = "Investigation   ";
            modInt = Integer.parseInt(modifierINT.getText());
            modifierInve.setText(Integer.toString(modInt + 2)); 
        }
        else {
            modifierInve.setText(modifierINT.getText());
            prof[8] = d;
        }
    }//GEN-LAST:event_checkB9StateChanged

    private void checkB10StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_checkB10StateChanged
        // Medicine proficiency checkbox selected/unselected in Skills window
        if (checkB10.isSelected()) {
            prof[9] = "Medicine   ";
            modWis = Integer.parseInt(modifierWIS.getText());
            modifierMedi.setText(Integer.toString(modWis + 2)); 
        }
        else {
            modifierMedi.setText(modifierWIS.getText());
            prof[9] = d;
        }
    }//GEN-LAST:event_checkB10StateChanged

    private void checkB11StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_checkB11StateChanged
        // Nature proficiency checkbox selected/unselected in Skills window
        if (checkB11.isSelected()) {
            prof[10] = "Nature   ";
            modInt = Integer.parseInt(modifierINT.getText());
            modifierNatu.setText(Integer.toString(modInt + 2));  
        }
        else {
            modifierNatu.setText(modifierINT.getText());
            prof[10] = d;
        }
    }//GEN-LAST:event_checkB11StateChanged

    private void checkB12StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_checkB12StateChanged
        // Perception
        if (checkB12.isSelected()) {
            prof[11] = "Perception   ";
            modWis = Integer.parseInt(modifierWIS.getText());
            modifierPerc.setText(Integer.toString(modWis + 2)); 
        }
        else {
            modifierPerc.setText(modifierWIS.getText());
            prof[11] = d;
        }
    }//GEN-LAST:event_checkB12StateChanged

    private void checkB13StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_checkB13StateChanged
        // Performance proficiency checkbox selected/unselected in Skills window
        if (checkB13.isSelected()) {
            prof[12] = "Performance   ";
            modCha = Integer.parseInt(modifierCHA.getText());
            modifierPerf.setText(Integer.toString(modCha + 2)); 
        }
        else {
            modifierPerf.setText(modifierCHA.getText());
            prof[12] = d;
        }
    }//GEN-LAST:event_checkB13StateChanged

    private void checkB14StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_checkB14StateChanged
        // Persuasion proficiency checkbox selected/unselected in Skills window
        if (checkB14.isSelected()) {
            prof[13] = "Persuasion   ";
            modCha = Integer.parseInt(modifierCHA.getText());
            modifierPers.setText(Integer.toString(modCha + 2)); 
        }
        else {
            modifierPers.setText(modifierCHA.getText());
            prof[13] = d;
        }
    }//GEN-LAST:event_checkB14StateChanged

    private void checkB15StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_checkB15StateChanged
        // Religion proficiency checkbox selected/unselected in Skills window
        if (checkB15.isSelected()) {
            prof[14] = "Religion   ";
            modInt = Integer.parseInt(modifierINT.getText());
            modifierReli.setText(Integer.toString(modInt + 2)); 
        }
        else {
            modifierReli.setText(modifierINT.getText());
            prof[14] = d;
        }
    }//GEN-LAST:event_checkB15StateChanged

    private void checkB16StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_checkB16StateChanged
        // Sleight of Hand proficiency checkbox selected/unselected in Skills window
        if (checkB16.isSelected()) {
            prof[15] = "Sleight of Hand   ";
            modDex = Integer.parseInt(modifierDEX.getText());
            modifierSlei.setText(Integer.toString(modDex + 2)); 
        }
        else {
            modifierSlei.setText(modifierDEX.getText());
            prof[15] = d;
        }
    }//GEN-LAST:event_checkB16StateChanged

    private void checkB17StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_checkB17StateChanged
        // Stealth proficiency checkbox selected/unselected in Skills window
        if (checkB17.isSelected()) {
            prof[16] = "Stealth   ";
            modDex = Integer.parseInt(modifierDEX.getText());
            modifierStea.setText(Integer.toString(modDex + 2)); 
        }
        else {
            modifierStea.setText(modifierDEX.getText());
            prof[16] = d;
        }
    }//GEN-LAST:event_checkB17StateChanged

    private void checkB18StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_checkB18StateChanged
        // Survival proficiency checkbox selected/unselected in Skills window
        if (checkB18.isSelected()) {
            prof[17] = "Survival   ";
            modWis = Integer.parseInt(modifierWIS.getText());
            modifierSurv.setText(Integer.toString(modWis + 2));  
        }
        else {
            modifierSurv.setText(modifierWIS.getText());
            prof[17] = d;
        }
    }//GEN-LAST:event_checkB18StateChanged

    private void menuNewCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuNewCActionPerformed
        /* When user chooses "New character" from the file menu, the program
         * basically does the same, as the "Reset" setting does.
         */
        if (JOptionPane.showConfirmDialog(this, "Create a new character? "
            + " All unsaved changes are lost.", "New character?", JOptionPane.YES_NO_OPTION)
            == JOptionPane.YES_OPTION) {
            
            // Resetting the name, race, class, background and alignment values
            textFieldName.setText("");
            comboBoxRace.setSelectedIndex(0);
            comboBoxClass.setSelectedIndex(0);
            comboBoxBackground.setSelectedIndex(0);
            comboBoxAlignment.setSelectedIndex(0);
            
            // Resetting the table values of basic information
            tableBasicI.getModel().setValueAt("", 0, 1);
            tableBasicI.getModel().setValueAt("", 1, 1);
            tableBasicI.getModel().setValueAt("", 2, 1);
            tableBasicI.getModel().setValueAt("", 3, 1);
            tableBasicI.getModel().setValueAt("", 4, 1);
            tableBasicI.getModel().setValueAt("", 5, 1);
            tableBasicI.getModel().setValueAt("", 6, 1);
            
            /* Resetting the personality traits, ideals, bonds, flaws, and features
             * & traits.
             */ 
            textAPersonality.setText("");
            textAIdeals.setText("");
            textABonds.setText("");
            textAFlaws.setText("");
            textAFeatsTraits.setText("");
            
            // Resetting the additional background story
            textAreaStory.setText("");
            
            // Resetting the ability scoring and printing the score
            spinnerSTR.setValue(0);
            spinnerDEX.setValue(0);
            spinnerCON.setValue(0);
            spinnerINT.setValue(0);
            spinnerWIS.setValue(0);
            spinnerCHA.setValue(0);
            labelSTR.setText("STR: ");
            labelDEX.setText("DEX: ");
            labelCON.setText("CON: ");
            labelINT.setText("INT: ");
            labelWIS.setText("WIS: ");
            labelCHA.setText("CHA: ");
            
            // Resetting the skill scoring
            modifierAcro.setText("");
            modifierAnim.setText("");
            modifierArca.setText("");
            modifierAthl.setText("");
            modifierDece.setText("");
            modifierHist.setText("");
            modifierInsi.setText("");
            modifierInti.setText("");
            modifierInve.setText("");
            modifierMedi.setText("");
            modifierNatu.setText("");
            modifierPerc.setText("");
            modifierPerf.setText("");
            modifierPers.setText("");
            modifierReli.setText("");
            modifierSlei.setText("");
            modifierStea.setText("");
            modifierSurv.setText("");
            
            // Resetting proficiencies and proficiency checkboxes
            proficiencies = "";
            resetProficiencies();
            
            // Removing image from the UI view
            picture1.setImage(null);
            picture1.repaint();
            
            // The new character needs to be saved as a new file
            buttonSave.setEnabled(false);
            menuSave.setEnabled(false);
        }      
    }//GEN-LAST:event_menuNewCActionPerformed

    private void font12StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_font12StateChanged
        // When font size 12 is selected from the Settings menu
        if (font12.isSelected()) {
            size = 12;
            setFontSize(size);         
        }
    }//GEN-LAST:event_font12StateChanged

    private void font14StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_font14StateChanged
        // When font size 14 is selected from the Settings menu
        if (font14.isSelected()) {
            size = 14;
            setFontSize(size);
        }
    }//GEN-LAST:event_font14StateChanged

    private void font16StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_font16StateChanged
        // When font size 16 is selected from the Settings menu
        if (font16.isSelected()) {
            size = 16;
            setFontSize(size);
        }
    }//GEN-LAST:event_font16StateChanged

    private void buttonUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonUndoActionPerformed
        /* Undo button pressed in Background Story dialog, triggers the same
         * action as the ctrl + Z keystroke would
         */       
        try {
            if (undo.canUndo()) {
                undo.undo();
            }
        } catch (CannotUndoException e) {
            
        }
    }//GEN-LAST:event_buttonUndoActionPerformed

    private void buttonRedoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRedoActionPerformed
        /* Redo button pressed in Background Story dialog, triggers the same
         * action as the ctrl + Y keystroke would
         */ 
        try {
            if (undo.canRedo()) {
                undo.redo();
            }
        } catch (CannotRedoException e) {
        }
    }//GEN-LAST:event_buttonRedoActionPerformed

    private void buttonSaveSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveSActionPerformed
        /* When user saves the Background Story, the copy is updated so that if
         * new changes are made after that, the earlier version can be returned. (
         * I forgot to create the event when creating events for other components
         * in Story dialog, that's why it's separate from them, sorry)
         */
        copyOfStory = textAreaStory.getText();
    }//GEN-LAST:event_buttonSaveSActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DnDCharacterCreator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DnDCharacterCreator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DnDCharacterCreator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DnDCharacterCreator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DnDCharacterCreator().setVisible(true);
            }
        });
    }
    
    /**
     * Method that updates a half-elf character's ability score stats. Keeps score
     * of chosen race bonuses, since there can be only two seleceted. Calls 
     * halfElfStatsChecker for checking the checkbox enable/disable status and
     * gives halfElfCounter as a parameter.
     */
    public void halfElfStatsUpdate() {
        int halfElfCounter = 0;
        
        if (halfElfSTR.isSelected()) {
            halfElfCounter++;
            halfElfStatsChecker(halfElfCounter); 
        }
        if (halfElfDEX.isSelected()) {
            halfElfCounter++;
            halfElfStatsChecker(halfElfCounter);
        }
        if (halfElfCON.isSelected()) {
            halfElfCounter++;
            halfElfStatsChecker(halfElfCounter);
        }
        if (halfElfINT.isSelected()) {
            halfElfCounter++;
            halfElfStatsChecker(halfElfCounter);
        }
        if (halfElfWIS.isSelected()) {
            halfElfCounter++;
            halfElfStatsChecker(halfElfCounter);
        }           
    }
    /**
     * Method that checks if user has already selected two of race bonus abilities.
     * When two checkboxes are selected, the chechboxes are disabled. If the user
     * unselects a checkbox, the checkbox availableness updates.
     * @param counter value of counter
     */
    public void halfElfStatsChecker(int counter) {
        if (counter >= 2) {
            halfElfSTR.setEnabled(halfElfSTR.isSelected());
            halfElfDEX.setEnabled(halfElfDEX.isSelected());
            halfElfCON.setEnabled(halfElfCON.isSelected());
            halfElfINT.setEnabled(halfElfINT.isSelected());
            halfElfWIS.setEnabled(halfElfWIS.isSelected());
        }
        else {
            halfElfSTR.setEnabled(true);
            halfElfDEX.setEnabled(true);
            halfElfCON.setEnabled(true);
            halfElfINT.setEnabled(true);
            halfElfWIS.setEnabled(true);
        }
    }
    
    /**
     * Method that updates the race bonus ability points.
     */
    public void updateRaceBonuses() {
        raceName = comboBoxRace.getSelectedItem().toString();
        CharacterStats charStats = new CharacterStats();
        charStats.setBonusPoints(raceName);
        // The race bonus ability points are set here
        bonusSTR.setText(Integer.toString(charStats.getBonusPoints(0)));
        bonusDEX.setText(Integer.toString(charStats.getBonusPoints(1)));
        bonusCON.setText(Integer.toString(charStats.getBonusPoints(2)));
        bonusINT.setText(Integer.toString(charStats.getBonusPoints(3)));
        bonusWIS.setText(Integer.toString(charStats.getBonusPoints(4)));
        bonusCHA.setText(Integer.toString(charStats.getBonusPoints(5)));
    }
       
    /**
     * Method that creates copies of the current spinner values. They can
     * be returned if user chooses Cancel in the Ability window.
     */
    public void copySpinnerValues() {
        copyStr = (Integer) spinnerSTR.getValue();
        copyDex = (Integer) spinnerDEX.getValue();
        copyCon = (Integer) spinnerCON.getValue();
        copyInt = (Integer) spinnerINT.getValue();
        copyWis = (Integer) spinnerWIS.getValue();
        copyCha = (Integer) spinnerCHA.getValue();      
    }
   
    /**
     * Method that updates the ability points and modifier scores. The method
     * counts at first the total score of ability points based on the race bonus
     * points and spinner values. Later the method calls CharacterStats class 
     * for setting the right modifier point values.
     * 
     * With a use of JComponent, JFormattedTextField and DefaulktFormatter e.g.,
     * the method also recognizes spinner values, that are entered manually with
     * a keyboard.
     * 
     * After collecting spinner values, the method updates the total score value
     * with counting spinner values and the race bonus points together, and
     * calls getModifierPoints method from the CharacterStats class. The labels
     * of total score and modifier score are updated with new values.
     */
    
    public void countTotalAbilityAndModifierScore() {
        // Counting the total STR point and modifier score
         int bStr = Integer.parseInt(bonusSTR.getText());
         totalSTR.setText(Integer.toString(bStr + spinStr)); 


         CharacterStats charStr = new CharacterStats();
         charStr.setModifierPoints(spinStr + bStr);
         modifierSTR.setText(Integer.toString(charStr.getModifierPoints()));

         JComponent compStr = spinnerSTR.getEditor();
         JFormattedTextField fieldStr = (JFormattedTextField) compStr.getComponent(0);
         DefaultFormatter formatterStr = (DefaultFormatter) fieldStr.getFormatter();
         formatterStr.setCommitsOnValidEdit(true);
         spinnerSTR.addChangeListener(new ChangeListener() {
         @Override
             public void stateChanged(ChangeEvent e) {
                 // Updating the total score of Strength points
                 spinStr = (Integer) spinnerSTR.getValue();
                 totalSTR.setText(Integer.toString(spinStr + bStr));

                 // Updating the modifier points for Strength
                 charStr.setModifierPoints(spinStr + bStr);
                 modifierSTR.setText(Integer.toString(charStr.getModifierPoints())); 
             }  
         });
         // Counting the total DEX point and modifier score
         int bDex = Integer.parseInt(bonusDEX.getText());
         totalDEX.setText(Integer.toString(bDex + spinDex));

         CharacterStats charDex = new CharacterStats();
         charDex.setModifierPoints(spinDex + bDex);
         modifierDEX.setText(Integer.toString(charDex.getModifierPoints()));

         JComponent compDex = spinnerDEX.getEditor();
         JFormattedTextField fieldDex = (JFormattedTextField) compDex.getComponent(0);
         DefaultFormatter formatterDex = (DefaultFormatter) fieldDex.getFormatter();
         formatterDex.setCommitsOnValidEdit(true);
         spinnerDEX.addChangeListener(new ChangeListener() {
         @Override
             public void stateChanged(ChangeEvent e) {
                 // Updating the total score of Dexterity points
                 spinDex = (Integer) spinnerDEX.getValue();
                 totalDEX.setText(Integer.toString(spinDex + bDex));

                 // Updating the modifier points for Dexterity
                 charDex.setModifierPoints(spinDex + bDex);
                 modifierDEX.setText(Integer.toString(charDex.getModifierPoints()));
             }
         });
         // Counting the total CON point and modifier score
         int bCon = Integer.parseInt(bonusCON.getText());
         totalCON.setText(Integer.toString(bCon + spinCon));

         CharacterStats charCon = new CharacterStats();
         charCon.setModifierPoints(spinCon + bCon);
         modifierCON.setText(Integer.toString(charCon.getModifierPoints()));

         JComponent compCon = spinnerCON.getEditor();
         JFormattedTextField fieldCon = (JFormattedTextField) compCon.getComponent(0);
         DefaultFormatter formatterCon = (DefaultFormatter) fieldCon.getFormatter();
         formatterCon.setCommitsOnValidEdit(true);
         spinnerCON.addChangeListener(new ChangeListener() {
         @Override
             public void stateChanged(ChangeEvent e) {
                 // Updating the total score of Constitution points
                 spinCon = (Integer) spinnerCON.getValue();
                 totalCON.setText(Integer.toString(spinCon + bCon));

                 // Updating the modifier points for Constitution
                 charCon.setModifierPoints(spinCon + bCon);
              modifierCON.setText(Integer.toString(charCon.getModifierPoints()));
             }
         });
         // Counting the total INT point and modifier score
         int bInt = Integer.parseInt(bonusINT.getText());
         totalINT.setText(Integer.toString(bInt + spinInt));

         CharacterStats charInt = new CharacterStats();
         charInt.setModifierPoints(spinInt + bInt);
         modifierINT.setText(Integer.toString(charInt.getModifierPoints()));

         JComponent compInt = spinnerINT.getEditor();
         JFormattedTextField fieldInt = (JFormattedTextField) compInt.getComponent(0);
         DefaultFormatter formatterInt = (DefaultFormatter) fieldInt.getFormatter();
         formatterInt.setCommitsOnValidEdit(true);
         spinnerINT.addChangeListener(new ChangeListener() {
         @Override
              public void stateChanged(ChangeEvent e) {
                 // Updating the total score of Intelligence points
                 spinInt = (Integer) spinnerINT.getValue();
                 totalINT.setText(Integer.toString(spinInt + bInt));

                 // Updating the modifier points for Intelligence
                 charInt.setModifierPoints(spinInt + bInt);
                 modifierINT.setText(Integer.toString(charInt.getModifierPoints()));
             }
         });
         // Counting the total WIS point and modifier score
         int bWis = Integer.parseInt(bonusWIS.getText());
         totalWIS.setText(Integer.toString(bWis + spinWis));

         CharacterStats charWis = new CharacterStats();
         charWis.setModifierPoints(spinWis + bWis);
         modifierWIS.setText(Integer.toString(charWis.getModifierPoints()));

         JComponent compWis = spinnerWIS.getEditor();
         JFormattedTextField fieldWis = (JFormattedTextField) compWis.getComponent(0);
         DefaultFormatter formatterWis = (DefaultFormatter) fieldWis.getFormatter();
         formatterWis.setCommitsOnValidEdit(true);
         spinnerWIS.addChangeListener(new ChangeListener() {
         @Override
             public void stateChanged(ChangeEvent e) {
                 // Updating the total score of Wisdom points
                 spinWis = (Integer) spinnerWIS.getValue();
                 totalWIS.setText(Integer.toString(spinWis + bWis));

                 // Updating the modifier points for Wisdom
                 charWis.setModifierPoints(spinWis + bWis);
                 modifierWIS.setText(Integer.toString(charWis.getModifierPoints()));
             }
         });
         // Counting the total CHA point and modifier score
         int bCha = Integer.parseInt(bonusCHA.getText());
         totalCHA.setText(Integer.toString(bCha + spinCha));

         CharacterStats charCha = new CharacterStats();
         charCha.setModifierPoints(spinCha + bCha);
         modifierCHA.setText(Integer.toString(charCha.getModifierPoints()));

         JComponent compCha = spinnerCHA.getEditor();
         JFormattedTextField fieldCha = (JFormattedTextField) compCha.getComponent(0);
         DefaultFormatter formatterCha = (DefaultFormatter) fieldCha.getFormatter();
         formatterCha.setCommitsOnValidEdit(true);
         spinnerCHA.addChangeListener(new ChangeListener() {
         @Override
             public void stateChanged(ChangeEvent e) {
                 // Updating the total score of Charisma points
                 spinCha = (Integer) spinnerCHA.getValue();
                 totalCHA.setText(Integer.toString(spinCha + bCha));

                 // Updating the modifier points for Charisma
                 charCha.setModifierPoints(spinCha + bCha);
                 modifierCHA.setText(Integer.toString(charCha.getModifierPoints()));
             }
         });      
    }
    
    /**
     * The method gets total score of str, dex, con, int, wis and cha ability
     * points and the race name when a character is loaded from a file, and
     * ability spinner values and race bonus points must be updated again. The
     * spinner values are defined by subtracting race bonus points from total
     * points score.
     */
    public void updateSpinnersAndRaceBonuses() {
        CharacterStats dndC = new CharacterStats();
        String raceN = comboBoxRace.getSelectedItem().toString();
        dndC.setBonusPoints(raceN);
        // Race bonus points counting
        int bStr = dndC.getBonusPoints(0);
        int bDex = dndC.getBonusPoints(1);
        int bCon = dndC.getBonusPoints(2);
        int bInt = dndC.getBonusPoints(3);
        int bWis = dndC.getBonusPoints(4);
        int bCha = dndC.getBonusPoints(5);
        
        // Setting the race bonus points
        bonusSTR.setText(Integer.toString(bStr));
        bonusDEX.setText(Integer.toString(bDex));
        bonusCON.setText(Integer.toString(bCon));
        bonusINT.setText(Integer.toString(bInt));
        bonusWIS.setText(Integer.toString(bWis));
        bonusCHA.setText(Integer.toString(bCha));
        
        /* Now that the spinner values can be defined by subtracting race bonus
         * points from the total score
         */
        spinStr = ((Integer.parseInt(totalSTR.getText())) - bStr);
        spinnerSTR.setValue(spinStr);
        
        spinDex = ((Integer.parseInt(totalDEX.getText())) - bDex);
        spinnerDEX.setValue(spinDex);
        
        spinCon = ((Integer.parseInt(totalCON.getText())) - bCon);
        spinnerCON.setValue(spinCon);
        
        spinInt = ((Integer.parseInt(totalINT.getText())) - bInt);
        spinnerINT.setValue(spinInt);
        
        spinWis = ((Integer.parseInt(totalWIS.getText())) - bWis);
        spinnerWIS.setValue(spinWis);
        
        spinCha = ((Integer.parseInt(totalCHA.getText())) - bCha);
        spinnerCHA.setValue(spinCha);
        
        /* After updating the values from the file, it's necessary to enable editing
         * of the ability scoring again. The method countTotalAbilityAndModifierScore
         * is again usable after turning the boolean value.
         */
        oldCharacterFirstView = false;  
    }
    
    /**
     * Method that prints the modifier points into the main view of the program.
     */ 
    public void printAbilityModifierPoints () {
        labelSTR.setText("STR: " + modifierSTR.getText());
        labelDEX.setText("DEX: " + modifierDEX.getText());
        labelCON.setText("CON: " + modifierCON.getText());
        labelINT.setText("INT: " + modifierINT.getText());
        labelWIS.setText("WIS: " + modifierWIS.getText());
        labelCHA.setText("CHA: " + modifierCHA.getText());
    }
    
    /**
     * Method that prints the modifier points directly for skill points. 
     * 
     * The skill points are always determined by modifier points and proficiency
     * bonus (which is +2 at level 1 of a character). Every skill is linked
     * to one of the six abilities (STR, DEX, CON, INT, WIS, CHA), and the 
     * modifier point of the ability, together with possible proficiency bonus,
     * defines the score of the skill points. The proficiency bonus is however
     * added to the total of skill points with an additional method.
     */
    public void countSkillPoints() {
        modifierAcro.setText(modifierDEX.getText());
        modifierAnim.setText(modifierWIS.getText());
        modifierArca.setText(modifierINT.getText());
        modifierAthl.setText(modifierSTR.getText());
        modifierDece.setText(modifierCHA.getText());
        modifierHist.setText(modifierINT.getText());
        modifierInsi.setText(modifierWIS.getText());
        modifierInti.setText(modifierCHA.getText());
        modifierInve.setText(modifierINT.getText());
        modifierMedi.setText(modifierWIS.getText());
        modifierNatu.setText(modifierINT.getText());
        modifierPerc.setText(modifierWIS.getText());
        modifierPerf.setText(modifierCHA.getText());
        modifierPers.setText(modifierCHA.getText());
        modifierReli.setText(modifierINT.getText());
        modifierSlei.setText(modifierDEX.getText());
        modifierStea.setText(modifierDEX.getText());
        modifierSurv.setText(modifierWIS.getText());
    }
    /**
     * Method that is used if ability points have changed after last view of the 
     * Skills. The skills chosen to proficiencies are also in a need of updating.
     */
    public void updateSkillPoints() {
        // If the ability modifiers has changed, the points should be updated
        countSkillPoints();
        
        // If STR modifier has changed
        if (!copyOfSTR.equals(modifierSTR.getText())) {
            modStr = Integer.parseInt(modifierSTR.getText());
            if (checkB4.isSelected() == true) {
                modifierAthl.setText(Integer.toString(modStr + 2));  
            }
            else {
                modifierAthl.setText(Integer.toString(modStr));
            }
            copyOfSTR = modifierSTR.getText();
        }
        // If DEX modifier has changed
        if (!copyOfDEX.equals(modifierDEX.getText())) {
            modDex = Integer.parseInt(modifierDEX.getText());
            if (checkB1.isSelected() == true) {
                modifierAcro.setText(Integer.toString(modDex + 2));
            }
            else {
                modifierAcro.setText(Integer.toString(modDex));
            }
            if (checkB16.isSelected() == true) {
                modifierSlei.setText(Integer.toString(modDex + 2));
            }
            else {
                modifierSlei.setText(Integer.toString(modDex));
            }
            
            if (checkB17.isSelected() == true) {
                modifierStea.setText(Integer.toString(modDex + 2));
            }
            else {
                modifierStea.setText(Integer.toString(modDex));
            }
            copyOfDEX = modifierDEX.getText();
        }
        // If INT modifier has changed
        if (!copyOfINT.equals(modifierINT.getText())) {
            modInt = Integer.parseInt(modifierINT.getText());
            if (checkB3.isSelected() == true) {
                modifierArca.setText(Integer.toString(modInt + 2));  
            }
            else {
                modifierArca.setText(Integer.toString(modInt));
            }
            if (checkB6.isSelected() == true) {
                modifierHist.setText(Integer.toString(modInt + 2));
            }
            else {
                modifierHist.setText(Integer.toString(modInt));
            }
            if (checkB9.isSelected() == true) {
                modifierInve.setText(Integer.toString(modInt + 2));
            }
            else {
                modifierInve.setText(Integer.toString(modInt));
            }
            if (checkB11.isSelected() == true) {
                modifierNatu.setText(Integer.toString(modInt + 2));
            }
            else {
                modifierNatu.setText(Integer.toString(modInt));
            }
            if (checkB15.isSelected() == true) {
                modifierReli.setText(Integer.toString(modInt + 2));
            }
            else {
                modifierReli.setText(Integer.toString(modInt));
            }
            copyOfINT = modifierINT.getText();
        }
        // If WIS modifier has changed
        if (!copyOfWIS.equals(modifierWIS.getText())) {
            modWis = Integer.parseInt(modifierWIS.getText());
            if (checkB2.isSelected() == true) {
                modifierAnim.setText(Integer.toString(modWis + 2));    
            }
            else {
                modifierAnim.setText(Integer.toString(modWis));
            }
            if (checkB7.isSelected() == true) {
                modifierInsi.setText(Integer.toString(modWis + 2));
            }
            else {
                modifierInsi.setText(Integer.toString(modWis));
            }
            if (checkB10.isSelected() == true) {
                modifierMedi.setText(Integer.toString(modWis + 2));
            }
            else {
                modifierMedi.setText(Integer.toString(modWis));
            }
            if (checkB12.isSelected() == true) {
                modifierPerc.setText(Integer.toString(modWis + 2));
            }
            else {
                modifierPerc.setText(Integer.toString(modWis));
            }
            if (checkB18.isSelected() == true) {
                modifierSurv.setText(Integer.toString(modWis + 2));
            }
            else {
                modifierSurv.setText(Integer.toString(modWis));
            }
            copyOfWIS = modifierWIS.getText();
        }
        // If CHA modifier has changed
        if (!copyOfCHA.equals(modifierCHA.getText())) {
            modCha = Integer.parseInt(modifierCHA.getText());
            if (checkB5.isSelected() == true) {
                modifierDece.setText(Integer.toString(modCha + 2)); 
            }
            else {
                modifierDece.setText(Integer.toString(modCha));
            }
            if (checkB8.isSelected() == true) {
                modifierInti.setText(Integer.toString(modCha + 2));
            }
            else {
                modifierInti.setText(Integer.toString(modCha));
            }
            if (checkB13.isSelected() == true) {
                modifierPerf.setText(Integer.toString(modCha + 2));
            }
            else {
                modifierPerf.setText(Integer.toString(modCha));
            }
            if (checkB14.isSelected() == true) {
                modifierPers.setText(Integer.toString(modCha + 2));
            }
            else {
                modifierPers.setText(Integer.toString(modCha));
            }
            copyOfCHA = modifierCHA.getText();
        }        
    }
    
    /**
     * Method that resets the checkboxes, when class or background has been changed.
     * The reason for this is, that the old skills are not selected when the user
     * updates his choices of proficiencies.
     */
    public void resetProficiencies() {
        checkB1.setSelected(false);
        checkB2.setSelected(false);
        checkB3.setSelected(false);
        checkB4.setSelected(false);
        checkB5.setSelected(false);
        checkB6.setSelected(false);
        checkB7.setSelected(false);
        checkB8.setSelected(false);
        checkB9.setSelected(false);
        checkB10.setSelected(false);
        checkB11.setSelected(false);
        checkB12.setSelected(false);
        checkB13.setSelected(false);
        checkB14.setSelected(false);
        checkB15.setSelected(false);
        checkB16.setSelected(false);
        checkB17.setSelected(false);
        checkB18.setSelected(false);
    }
    
    /**
     * Method that lists all the chosen proficiencies into a String. This variable
     * is set to Character object with setProficiencies(), so that the saved txt
     * file has a list of chosen proficiencies, even thought returning them when
     * loading a character is not possible yet at this point.
     */
    public void listProficiencies() {
        proficiencies = " ";
        for (int i = 0; i <= 17; i++) {
            if (!prof[i].equals(d)) {
                proficiencies = proficiencies + prof[i];
            }
        }     
    }
    
    /**
     * Method that creates a Character object, and uses set() methods from the
     * Character class for saving all the input data of the character, in a form
     * of a txt file.
     */
    public void saveCharacter() {
        Character character = new Character();
        
        // Setting the character's name, race, class, background, alignment
        character.setName(textFieldName.getText());
        character.setRace(comboBoxRace.getSelectedItem().toString());
        character.setCharacterClass(comboBoxClass.getSelectedItem().toString());
        character.setBackground(comboBoxBackground.getSelectedItem().toString());
        character.setAlignment(comboBoxAlignment.getSelectedItem().toString());
       
        // Setting the the table's information of the character
        String tAge = (String)tableBasicI.getValueAt(0, 1);
        character.setAge(tAge);
        String tSex = (String)tableBasicI.getValueAt(1, 1);
        character.setSex(tSex);
        String tHeight = (String)tableBasicI.getValueAt(2, 1);
        character.setHeight(tHeight);
        String tWeight = (String)tableBasicI.getValueAt(3, 1);
        character.setWeight(tWeight);
        String tEyes = (String)tableBasicI.getValueAt(4, 1);
        character.setEyes(tEyes);
        String tSkin = (String)tableBasicI.getValueAt(5, 1);
        character.setSkin(tSkin);
        String tHair = (String)tableBasicI.getValueAt(6, 1);
        character.setHair(tHair);
        
        // Setting the personality traits, ideals, bonds, flaws, features & traits
        character.setPersonality(textAPersonality.getText());
        character.setIdeals(textAIdeals.getText());
        character.setBonds(textABonds.getText());
        character.setFlaws(textAFlaws.getText());
        character.setFeatsAndTraits(textAFeatsTraits.getText());
        
        // Setting the character's additional background story
        character.setBackgroundStory(textAreaStory.getText());
        
        // Setting the ability values
        character.setStrength(Integer.parseInt(totalSTR.getText()));
        character.setDexterity(Integer.parseInt(totalDEX.getText()));
        character.setConstitution(Integer.parseInt(totalCON.getText()));
        character.setIntelligence(Integer.parseInt(totalINT.getText()));
        character.setWisdom(Integer.parseInt(totalWIS.getText()));
        character.setCharisma(Integer.parseInt(totalCHA.getText()));
        
        // Setting the skill values and proficiencies
        character.setAcrobatics(modifierAcro.getText());
        character.setAnimalHandling(modifierAnim.getText());
        character.setArcana(modifierArca.getText());
        character.setAthletics(modifierAthl.getText());
        character.setDeception(modifierDece.getText());
        character.setHistory(modifierHist.getText());
        character.setInsight(modifierInsi.getText());
        character.setIntimidation(modifierInti.getText());
        character.setInvestigation(modifierInve.getText());
        character.setMedicine(modifierMedi.getText());
        character.setNature(modifierNatu.getText());
        character.setPerception(modifierPerc.getText());
        character.setPerformance(modifierPerf.getText());
        character.setPersuasion(modifierPers.getText());
        character.setReligion(modifierReli.getText());
        character.setSleightOfHand(modifierSlei.getText());
        character.setStealth(modifierStea.getText());
        character.setSurvival(modifierSurv.getText());
        
        character.setProficiencies(proficiencies);
        
        // If user uses "Save as..."
        if (saveAs == true) {
            JFileChooser chooser = new JFileChooser();
            int returnVal = chooser.showSaveDialog(getContentPane());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String fileName = chooser.getSelectedFile().getPath();
                setFileName(fileName);
                Character.saveCharacter(character, fileName);
                JOptionPane.showMessageDialog(getContentPane(), "Saved file " + 
                fileName, "Saved", JOptionPane.INFORMATION_MESSAGE);
                menuSave.setEnabled(true);
                buttonSave.setEnabled(true);
                }
        }
        // If user uses "Save"
        else {
            String fileName = getFileName();
            Character.saveCharacter(character, fileName);           
        }
    }
    
    /**
     * Get the value fileN
     * 
     * @return the value of fileN
     */
    public String getFileName() {
        return fileN;  
    }
    
    /**
     * Set the value of fileN
     * @param fileN new value of fileN
     */
    public void setFileName(String fileN) {
        this.fileN = fileN;
    }
    
    /**
     * Method that loads a character from a txt file and updates the UI with the
     * data stored in the loaded file.
     */
    public void loadCharacter() {
        try {
            /* So that activating Ability button does not do anything (reseting info
             * when creating using another object), change of boolean value is needed.
             * This boolean is used in buttonAbilitiesActionPerformed() and methods
             * that active, when spinner values are changed.
             */ 
            oldCharacterFirstView = true;
            /* Also with skills, it's important to notify the program, that it should
             * not accidentally reset the skill modifiers. This boolean variable is
             * used in buttonSkillsActionPerformed() method.
             */
            firstSkillView = false;
            /* Also because of the need of informative popup window, this boolean 
             * value is changed.
             */
            oldCSkills = true;
            /* Since the UI notices that the race, class and background might have
             * been changing when loading a file, the boolean for managing the function
             * of the comboboxes' Action Listener is set here
             */
            loadedFile = true;

            JFileChooser chooser = new JFileChooser();
            int returnVal = chooser.showOpenDialog(getContentPane());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String fileName = chooser.getSelectedFile().getPath();
                setFileName(fileName);
                Character dndC = new Character();
                dndC = Character.loadCharacter(fileName);
                CharacterStats dndCStats = new CharacterStats();

                // Setting all the values from the file, to the interface
                textFieldName.setText(dndC.getName());
                /* Getting combo box information (race, class, background, alignment)
                 * needs to use CharacterStats class's help, as the file does not
                 * include the index values of the combo boxes.
                 */
                String comboRace = dndC.getRace();
                dndCStats.setComboBoxRaceValue(comboRace);
                comboBoxRace.setSelectedIndex(dndCStats.getComboBoxRaceValue());

                String comboClass = dndC.getCharacterClass();
                dndCStats.setComboBoxClassValue(comboClass);
                comboBoxClass.setSelectedIndex(dndCStats.getComboBoxClassValue());

                String comboBG = dndC.getBackground();
                dndCStats.setComboBoxBGValue(comboBG);
                comboBoxBackground.setSelectedIndex(dndCStats.getComboBoxBGValue());

                String comboAlignment = dndC.getAlignment();
                dndCStats.setComboBoxAlignmentValue(comboAlignment);
                comboBoxAlignment.setSelectedIndex(dndCStats.getComboBoxAlignmentValue());

                /* The class and background are copied to the copy variables, so that
                 * the UI does not use possible old values when viewing skills after
                 * loading a file.
                 */
                copyClassN = dndC.getCharacterClass();
                copyBackground = dndC.getBackground();

                textAPersonality.setText(dndC.getPersonality());
                textAIdeals.setText(dndC.getIdeals());
                textABonds.setText(dndC.getBonds());
                textAFlaws.setText(dndC.getFlaws());
                textAFeatsTraits.setText(dndC.getFeatsAndTraits());

                textAreaStory.setText(dndC.getBackgroundStory());

                String tAge = dndC.getAge();
                tableBasicI.setValueAt(tAge, 0, 1);
                String tSex = dndC.getSex();
                tableBasicI.setValueAt(tSex, 1, 1);
                String tHeight = dndC.getHeight();
                tableBasicI.setValueAt(tHeight, 2, 1);
                String tWeight = dndC.getWeight();
                tableBasicI.setValueAt(tWeight, 3, 1);
                String tEyes = dndC.getEyes();
                tableBasicI.setValueAt(tEyes, 4, 1);
                String tSkin = dndC.getSkin();
                tableBasicI.setValueAt(tSkin, 5, 1);
                String tHair = dndC.getHair();
                tableBasicI.setValueAt(tHair, 6, 1);

                /* Ability scores need to be updated in spinners and race bonus
                 * points, as only the total score of the abilities is saved into a
                 * file. The spinner values and race bonuses are updated in the 
                 * method updateSpinnerAndRaceBonuses, which is called when the user
                 * activates Ability button for the very first time after loading
                 * an old character.
                 */
                int totalStr = dndC.getStrength();
                totalSTR.setText(Integer.toString(totalStr));
                int totalDex = dndC.getDexterity();
                totalDEX.setText(Integer.toString(totalDex));
                int totalCon = dndC.getConstitution();
                totalCON.setText(Integer.toString(totalCon));
                int totalInt = dndC.getIntelligence();
                totalINT.setText(Integer.toString(totalInt));
                int totalWis = dndC.getWisdom();
                totalWIS.setText(Integer.toString(totalWis));
                int totalCha = dndC.getCharisma();
                totalCHA.setText(Integer.toString(totalCha));

                // Modifiers
                dndCStats.setModifierPoints(totalStr);
                modifierSTR.setText(Integer.toString(dndCStats.getModifierPoints()));
                dndCStats.setModifierPoints(totalDex);
                modifierDEX.setText(Integer.toString(dndCStats.getModifierPoints()));
                dndCStats.setModifierPoints(totalCon);
                modifierCON.setText(Integer.toString(dndCStats.getModifierPoints()));
                dndCStats.setModifierPoints(totalInt);
                modifierINT.setText(Integer.toString(dndCStats.getModifierPoints()));
                dndCStats.setModifierPoints(totalWis);
                modifierWIS.setText(Integer.toString(dndCStats.getModifierPoints()));
                dndCStats.setModifierPoints(totalCha);
                modifierCHA.setText(Integer.toString(dndCStats.getModifierPoints()));

                // Updating the printed modifiers.
                printAbilityModifierPoints();

                // Skill points
                modifierAcro.setText(dndC.getAcrobatics());
                modifierAnim.setText(dndC.getAnimalHandling());
                modifierArca.setText(dndC.getArcana());
                modifierAthl.setText(dndC.getAthletics());
                modifierDece.setText(dndC.getDeception());
                modifierHist.setText(dndC.getHistory());
                modifierInsi.setText(dndC.getInsight());
                modifierInti.setText(dndC.getIntimidation());
                modifierInve.setText(dndC.getInvestigation());
                modifierMedi.setText(dndC.getMedicine());
                modifierNatu.setText(dndC.getNature());
                modifierPerc.setText(dndC.getPerception());
                modifierPerf.setText(dndC.getPerformance());
                modifierPers.setText(dndC.getPersuasion());
                modifierReli.setText(dndC.getReligion());
                modifierSlei.setText(dndC.getSleightOfHand());
                modifierStea.setText(dndC.getStealth());
                modifierSurv.setText(dndC.getSurvival());

                /* The proficiencies are asked to be chosen again by the user, since
                 * I tried for so long to find a way to return the proficiency checkbox
                 * values without success (more about this in the report). The list
                 * of the original proficiencies chosen before saving a file are still
                 * in the txt file though, so user has them stored, but the proficiencies
                 * can also be chosen differently after loading and possibly saved again.
                 * All the checkboxes are set as not selected, so that they can be
                 * selected again by user.
                 */
                resetProficiencies();
                
                // Possible old picture is removed for now
                picture1.setImage(null);
                picture1.repaint();
                
                // Since the proficiencies must be set again, use Save as first
                buttonSave.setEnabled(false);
                menuSave.setEnabled(false);
                
                // The user is informed about the need of updating the proficiencies
                informChoosingProficiencies();
                
                // Boolean for changing the race is again updated.
                loadedFile = false;
                
            }
        } catch (NullPointerException e) {
            throw new IllegalStateException("Could not load a character file", e);
        }
    }
    
    /**
     * Method that sets the UI components' font size.
     * @param size the value of size
     */
    public void setFontSize(int size) {
        // Setting the font size with given parameter (12/14/16)
        Font currentFont = myFont.deriveFont(Font.PLAIN, size);
        bonusCHA.setFont(currentFont);
        bonusCON.setFont(currentFont);
        bonusDEX.setFont(currentFont);
        bonusINT.setFont(currentFont);
        bonusSTR.setFont(currentFont);
        bonusWIS.setFont(currentFont);
        buttonACancel.setFont(currentFont);
        buttonASC.setFont(currentFont);
        buttonAbilities.setFont(currentFont);
        buttonCancel.setFont(currentFont);
        buttonCancelS.setFont(currentFont);
        buttonPicture.setFont(currentFont);
        buttonRedo.setFont(currentFont);
        buttonSOk.setFont(currentFont);
        buttonSave.setFont(currentFont);
        buttonSaveAs.setFont(currentFont);
        buttonSaveClose.setFont(currentFont);
        buttonSaveS.setFont(currentFont);
        buttonSkills.setFont(currentFont);
        buttonStory.setFont(currentFont);
        buttonUndo.setFont(currentFont);
        checkB1.setFont(currentFont);
        checkB10.setFont(currentFont);
        checkB11.setFont(currentFont);
        checkB12.setFont(currentFont);
        checkB13.setFont(currentFont);
        checkB14.setFont(currentFont);
        checkB15.setFont(currentFont);
        checkB16.setFont(currentFont);
        checkB17.setFont(currentFont);
        checkB18.setFont(currentFont);
        checkB2.setFont(currentFont);
        checkB3.setFont(currentFont);
        checkB4.setFont(currentFont);
        checkB5.setFont(currentFont);
        checkB6.setFont(currentFont);
        checkB7.setFont(currentFont);
        checkB8.setFont(currentFont);
        checkB9.setFont(currentFont);
        comboBoxAlignment.setFont(currentFont);
        comboBoxBackground.setFont(currentFont);
        comboBoxClass.setFont(currentFont);
        comboBoxRace.setFont(currentFont);
        dialogAbilities.setFont(currentFont);
        dialogSkills.setFont(currentFont);
        dialogStory.setFont(currentFont);
        disableEditSettings.setFont(currentFont);
        font12.setFont(currentFont);
        font14.setFont(currentFont);
        font16.setFont(currentFont);
        halfElfCON.setFont(currentFont);
        halfElfDEX.setFont(currentFont);
        halfElfINT.setFont(currentFont);
        halfElfInfoOne.setFont(currentFont);
        halfElfInfoThree.setFont(currentFont);
        halfElfInfoTwo.setFont(currentFont);
        halfElfSTR.setFont(currentFont);
        halfElfStats.setFont(currentFont);
        halfElfWIS.setFont(currentFont);
        instructionsOne.setFont(currentFont);
        instructionsThree.setFont(currentFont);
        instructionsTwo.setFont(currentFont);
        jLabel1.setFont(currentFont);
        jLabel2.setFont(currentFont);
        jMenuBar1.setFont(currentFont);
        jScrollPane1.setFont(currentFont);
        jScrollPane2.setFont(currentFont);
        jScrollPane3.setFont(currentFont);
        jScrollPane4.setFont(currentFont);
        jScrollPane5.setFont(currentFont);
        jScrollPane6.setFont(currentFont);
        jScrollPane7.setFont(currentFont);
        jScrollPane8.setFont(currentFont);
        jScrollPane9.setFont(currentFont);
        labelAcro.setFont(currentFont);
        labelAnim.setFont(currentFont);
        labelArca.setFont(currentFont);
        labelAthl.setFont(currentFont);
        labelBackground.setFont(currentFont);
        labelBasicI.setFont(currentFont);
        labelBonds.setFont(currentFont);
        labelCHA.setFont(currentFont);
        labelCON.setFont(currentFont);
        labelCR.setFont(currentFont);
        labelCharisma.setFont(currentFont);
        labelClass.setFont(currentFont);
        labelClassProficiencies.setFont(currentFont);
        labelConstitution.setFont(currentFont);
        labelDEX.setFont(currentFont);
        labelDece.setFont(currentFont);
        labelDexterity.setFont(currentFont);
        labelFeatsTraits.setFont(currentFont);
        labelFlaws.setFont(currentFont);
        labelHist.setFont(currentFont);
        labelINT.setFont(currentFont);
        labelIdeals.setFont(currentFont);
        labelInsi.setFont(currentFont);
        labelInstructions.setFont(currentFont);
        labelIntelligence.setFont(currentFont);
        labelInti.setFont(currentFont);
        labelInve.setFont(currentFont);
        labelMedi.setFont(currentFont);
        labelName.setFont(currentFont);
        labelNatu.setFont(currentFont);
        labelPerc.setFont(currentFont);
        labelPerf.setFont(currentFont);
        labelPers.setFont(currentFont);
        labelPersonality.setFont(currentFont);
        labelPoints.setFont(currentFont);
        labelProficiency.setFont(currentFont);
        labelRB.setFont(currentFont);
        labelRace.setFont(currentFont);
        labelReli.setFont(currentFont);
        labelSBackg.setFont(currentFont);
        labelSTR.setFont(currentFont);
        labelSlei.setFont(currentFont);
        labelStea.setFont(currentFont);
        labelStrenght.setFont(currentFont);
        labelSurv.setFont(currentFont);
        labelTotal.setFont(currentFont);
        labelWIS.setFont(currentFont);
        labelWisdom.setFont(currentFont);
        menuDisableE.setFont(currentFont);
        menuExit.setFont(currentFont);
        menuFile.setFont(currentFont);
        menuFont.setFont(currentFont);
        menuLoadC.setFont(currentFont);
        menuNewC.setFont(currentFont);
        menuResetC.setFont(currentFont);
        menuSave.setFont(currentFont);
        menuSaveAs.setFont(currentFont);
        modifierAcro.setFont(currentFont);
        modifierAnim.setFont(currentFont);
        modifierArca.setFont(currentFont);
        modifierAthl.setFont(currentFont);
        modifierCHA.setFont(currentFont);
        modifierCON.setFont(currentFont);
        modifierDEX.setFont(currentFont);
        modifierDece.setFont(currentFont);
        modifierHist.setFont(currentFont);
        modifierINT.setFont(currentFont);
        modifierInsi.setFont(currentFont);
        modifierInti.setFont(currentFont);
        modifierInve.setFont(currentFont);
        modifierMedi.setFont(currentFont);
        modifierNatu.setFont(currentFont);
        modifierPerc.setFont(currentFont);
        modifierPerf.setFont(currentFont);
        modifierPers.setFont(currentFont);
        modifierReli.setFont(currentFont);
        modifierSTR.setFont(currentFont);
        modifierSlei.setFont(currentFont);
        modifierStea.setFont(currentFont);
        modifierSurv.setFont(currentFont);
        modifierWIS.setFont(currentFont);
        spinnerCHA.setFont(currentFont);
        spinnerCON.setFont(currentFont);
        spinnerDEX.setFont(currentFont);
        spinnerINT.setFont(currentFont);
        spinnerSTR.setFont(currentFont);
        spinnerWIS.setFont(currentFont);
        tableBasicI.setFont(currentFont);
        textABonds.setFont(currentFont);
        textAFeatsTraits.setFont(currentFont);
        textAFlaws.setFont(currentFont);
        textAIdeals.setFont(currentFont);
        textAPersonality.setFont(currentFont);
        textAreaStory.setFont(currentFont);
        textFieldName.setFont(currentFont);
        textPaneSkillsB.setFont(currentFont);
        textPaneSkillsC.setFont(currentFont);
        totalCHA.setFont(currentFont);
        totalCON.setFont(currentFont);
        totalDEX.setFont(currentFont);
        totalINT.setFont(currentFont);
        totalSTR.setFont(currentFont);
        totalWIS.setFont(currentFont);        
    }
    
    /**
     * Method that activates when user has opened the Skills window the first time
     * after loading a character. It notifies the user to choose the skill proficiencies
     * again, since they are not loaded from the file.
     */
    private void informChoosingProficiencies() {
        JOptionPane.showMessageDialog(this, "Please choose the character's " +
        "skill proficiencies again", "Choose the skill proficiencies",
        JOptionPane.INFORMATION_MESSAGE);   
    }
    
    private void informChangedAbilities() {
        JOptionPane.showMessageDialog(dialogSkills, "You have changed your character's"
        + " race, therefore please save your Ability points again", " ",
        JOptionPane.INFORMATION_MESSAGE);
    }
    
                   
    /** Method that is used to confirmation of canceling changes and exiting or
     *  exiting the program.
     */
    private void confirmCancelOrExit() {
        if (JOptionPane.showConfirmDialog(this, "Close the character"
            + " creator? All unsaved changes are lost", "Do you want to close the"
             + " program?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            
            /* The dialogs are set visible, because if they where not opened
             * during the use of program, their diposing causes errors.
             */
            dialogStory.setVisible(true);
            dialogStory.dispose();   
            dialogAbilities.setVisible(true);
            dialogAbilities.dispose();
            dialogSkills.setVisible(true);
            dialogSkills.dispose();
            halfElfStats.setVisible(true);
            halfElfStats.dispose();
            this.dispose();           
         }      
    }
    
    /** These methods use JOptionPane dialog for cancelling/closing confirmation
     *  when interarcting with Abilities, Skills and Additional background story
     *  windows.
     */
    private void abilitiesConfirmExit() {
        if (JOptionPane.showConfirmDialog(dialogAbilities, "Close the Ability"
            + " window? All unsaved changes are lost", "Do you want to close the"
            + " window?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            dialogAbilities.setVisible(false);
            // If some of the changes are unsaved, the last version values are set here
            spinnerSTR.setValue(copyStr);
            spinnerDEX.setValue(copyDex);
            spinnerCON.setValue(copyCon);
            spinnerINT.setValue(copyInt);
            spinnerWIS.setValue(copyWis);
            spinnerCHA.setValue(copyCha);
        }          
    }
    
    private void skillsConfirmExit() {
        if (JOptionPane.showConfirmDialog(dialogSkills, "Do you want to close the"
        + " Skills window?", "Close the Skills window?", 
        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            dialogSkills.setVisible(false);
        }          
    }
    
    private void storyConfirmExit() {
        if (JOptionPane.showConfirmDialog(dialogStory, "Close the Story"
            + " window? All unsaved changes are lost", "Do you want to close the"
            + " window?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            textAreaStory.setText(copyOfStory);
            dialogStory.setVisible(false);
        }          
    }
    
    // Variables
    // The name of a character's race
    String raceName = "";
    // The name of a character's class
    String className = "";
    // The name of a character's background
    String background = "";
    /* Boolean that is false by default, but changes value if user loads a character
     * txt file. Controls usage of different methods, updating ability scoring.
     */
    // Default value, that is setted to the total ability score in the beginning.
    int defaultValue = 0;
    
    /* Boolean that is used when user loads an old character. The ability points
     * are managed differently than in other cases.
     */
    boolean oldCharacterFirstView = false;
    /* Boolean that is used when user loads old character from a file and views
     * skills after that.
     */
    boolean oldCSkills = false;
    /* Boolean that is used to not alarm the user to save abilities/skills again,
     * when loading a character file.
     */
    boolean loadedFile = false;
    // Copies of ability modifiers, which may be compared to changed values later
    String copyOfSTR = "";
    String copyOfDEX = "";
    String copyOfINT = "";
    String copyOfWIS = "";
    String copyOfCHA = "";
    
    // Variable that controls the file saving/overwriting exicting file
    boolean saveAs = true;
    
    // The saved file's name
    String fileN;
    
    // Creating a font object for managing the font size
    Font myFont = new Font("Tahoma", Font.PLAIN, 12);
    // The size of the font
    int size = 12;
    
    // Spinner values for counting the score
    int spinStr = 0;
    int spinDex = 0;
    int spinCon = 0;
    int spinInt = 0;
    int spinWis = 0;
    int spinCha = 0;
    
    // Copy variables of the spinner values, used with cancel function in Abilities
    int copyStr = 0;
    int copyDex = 0;
    int copyCon = 0;
    int copyInt = 0;
    int copyWis = 0;
    int copyCha = 0;
    
    // New UndoManager
    UndoManager undo;
    // Document used with undo/redo in background story JTextArea
    Document story;
    
    /* A copy of the background story, that is updated when user opens the 
     * story dialog. If the changes are cancelled, the copy's value is returned
     */
    String copyOfStory = "";
    
    // List of proficiencies (names), that user has selected
    String proficiencies = " ";
    /* Variable, that helps with constructing the proficiencies variable. Used in
     * the array presented below 
     */
    String d = "notProficiency";
    /* An array that is by default "empty". In the end of selecting proficiency
     * skills, this array is used for listing the variable proficiencies. If the
     * value in index x is not d, the value is added to the proficiencies value.
     * If the value in index x is d, it is ignored.
     */
    String[] prof = {d, d, d, d, d, d, d, d, d, d, d, d, d, d, d, d, d, d};
    
    /* In case of ability modifier changing, this boolean is used for controlling
     * the use of methods updating the Skill points view
     */
    boolean firstSkillView = true;
    
    /* In case of the race, class or background changes, the class name and background
     * needs to be compared to copied values.
     */
    String copyRace = "";    
    String copyClassN = "";
    String copyBackground = "";
    
    // Modifier values, used with spinners and in Skill points management
    int modStr;
    int modDex;
    int modCon;
    int modInt;
    int modWis;
    int modCha;
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel abilityPanel;
    private javax.swing.JLabel bonusCHA;
    private javax.swing.JLabel bonusCON;
    private javax.swing.JLabel bonusDEX;
    private javax.swing.JLabel bonusINT;
    private javax.swing.JLabel bonusSTR;
    private javax.swing.JLabel bonusWIS;
    private javax.swing.JButton buttonACancel;
    private javax.swing.JButton buttonASC;
    private javax.swing.JButton buttonAbilities;
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonCancelS;
    private javax.swing.JButton buttonPicture;
    private javax.swing.JButton buttonRedo;
    private javax.swing.JButton buttonSOk;
    private javax.swing.JButton buttonSave;
    private javax.swing.JButton buttonSaveAs;
    private javax.swing.JButton buttonSaveClose;
    private javax.swing.JButton buttonSaveS;
    private javax.swing.JButton buttonSkills;
    private javax.swing.JButton buttonStory;
    private javax.swing.JButton buttonUndo;
    private javax.swing.JCheckBox checkB1;
    private javax.swing.JCheckBox checkB10;
    private javax.swing.JCheckBox checkB11;
    private javax.swing.JCheckBox checkB12;
    private javax.swing.JCheckBox checkB13;
    private javax.swing.JCheckBox checkB14;
    private javax.swing.JCheckBox checkB15;
    private javax.swing.JCheckBox checkB16;
    private javax.swing.JCheckBox checkB17;
    private javax.swing.JCheckBox checkB18;
    private javax.swing.JCheckBox checkB2;
    private javax.swing.JCheckBox checkB3;
    private javax.swing.JCheckBox checkB4;
    private javax.swing.JCheckBox checkB5;
    private javax.swing.JCheckBox checkB6;
    private javax.swing.JCheckBox checkB7;
    private javax.swing.JCheckBox checkB8;
    private javax.swing.JCheckBox checkB9;
    private javax.swing.JComboBox<String> comboBoxAlignment;
    private javax.swing.JComboBox<String> comboBoxBackground;
    private javax.swing.JComboBox<String> comboBoxClass;
    private javax.swing.JComboBox<String> comboBoxRace;
    private javax.swing.JDialog dialogAbilities;
    private javax.swing.JDialog dialogSkills;
    private javax.swing.JDialog dialogStory;
    private javax.swing.JMenu disableEditSettings;
    private javax.swing.JRadioButtonMenuItem font12;
    private javax.swing.JRadioButtonMenuItem font14;
    private javax.swing.JRadioButtonMenuItem font16;
    private javax.swing.ButtonGroup fontSButtonGroup;
    private javax.swing.JCheckBox halfElfCON;
    private javax.swing.JCheckBox halfElfDEX;
    private javax.swing.JCheckBox halfElfINT;
    private javax.swing.JLabel halfElfInfoOne;
    private javax.swing.JLabel halfElfInfoThree;
    private javax.swing.JLabel halfElfInfoTwo;
    private javax.swing.JCheckBox halfElfSTR;
    private javax.swing.JDialog halfElfStats;
    private javax.swing.JCheckBox halfElfWIS;
    private javax.swing.JLabel header;
    private javax.swing.JLabel instructionsOne;
    private javax.swing.JLabel instructionsThree;
    private javax.swing.JLabel instructionsTwo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JLabel labelAbilities;
    private javax.swing.JLabel labelAcro;
    private javax.swing.JLabel labelAnim;
    private javax.swing.JLabel labelArca;
    private javax.swing.JLabel labelAthl;
    private javax.swing.JLabel labelBackground;
    private javax.swing.JLabel labelBasicI;
    private javax.swing.JLabel labelBonds;
    private javax.swing.JLabel labelCHA;
    private javax.swing.JLabel labelCON;
    private javax.swing.JLabel labelCR;
    private javax.swing.JLabel labelCharisma;
    private javax.swing.JLabel labelClass;
    private javax.swing.JLabel labelClassProficiencies;
    private javax.swing.JLabel labelConstitution;
    private javax.swing.JLabel labelDEX;
    private javax.swing.JLabel labelDece;
    private javax.swing.JLabel labelDexterity;
    private javax.swing.JLabel labelDialogStoryHeader;
    private javax.swing.JLabel labelFeatsTraits;
    private javax.swing.JLabel labelFlaws;
    private javax.swing.JLabel labelHist;
    private javax.swing.JLabel labelINT;
    private javax.swing.JLabel labelIdeals;
    private javax.swing.JLabel labelInsi;
    private javax.swing.JLabel labelInstructions;
    private javax.swing.JLabel labelIntelligence;
    private javax.swing.JLabel labelInti;
    private javax.swing.JLabel labelInve;
    private javax.swing.JLabel labelMedi;
    private javax.swing.JLabel labelName;
    private javax.swing.JLabel labelNatu;
    private javax.swing.JLabel labelPerc;
    private javax.swing.JLabel labelPerf;
    private javax.swing.JLabel labelPers;
    private javax.swing.JLabel labelPersonality;
    private javax.swing.JLabel labelPoints;
    private javax.swing.JLabel labelProficiency;
    private javax.swing.JLabel labelRB;
    private javax.swing.JLabel labelRace;
    private javax.swing.JLabel labelReli;
    private javax.swing.JLabel labelSBackg;
    private javax.swing.JLabel labelSTR;
    private javax.swing.JLabel labelSkills;
    private javax.swing.JLabel labelSlei;
    private javax.swing.JLabel labelStea;
    private javax.swing.JLabel labelStrenght;
    private javax.swing.JLabel labelSurv;
    private javax.swing.JLabel labelTotal;
    private javax.swing.JLabel labelWIS;
    private javax.swing.JLabel labelWisdom;
    private javax.swing.JCheckBoxMenuItem menuDisableE;
    private javax.swing.JMenuItem menuExit;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenu menuFont;
    private javax.swing.JMenuItem menuLoadC;
    private javax.swing.JMenuItem menuNewC;
    private javax.swing.JMenuItem menuResetC;
    private javax.swing.JMenuItem menuSave;
    private javax.swing.JMenuItem menuSaveAs;
    private javax.swing.JLabel modifierAcro;
    private javax.swing.JLabel modifierAnim;
    private javax.swing.JLabel modifierArca;
    private javax.swing.JLabel modifierAthl;
    private javax.swing.JLabel modifierCHA;
    private javax.swing.JLabel modifierCON;
    private javax.swing.JLabel modifierDEX;
    private javax.swing.JLabel modifierDece;
    private javax.swing.JLabel modifierHist;
    private javax.swing.JLabel modifierINT;
    private javax.swing.JLabel modifierInsi;
    private javax.swing.JLabel modifierInti;
    private javax.swing.JLabel modifierInve;
    private javax.swing.JLabel modifierMedi;
    private javax.swing.JLabel modifierNatu;
    private javax.swing.JLabel modifierPerc;
    private javax.swing.JLabel modifierPerf;
    private javax.swing.JLabel modifierPers;
    private javax.swing.JLabel modifierReli;
    private javax.swing.JLabel modifierSTR;
    private javax.swing.JLabel modifierSlei;
    private javax.swing.JLabel modifierStea;
    private javax.swing.JLabel modifierSurv;
    private javax.swing.JLabel modifierWIS;
    private Project.Picture picture1;
    private javax.swing.JSpinner spinnerCHA;
    private javax.swing.JSpinner spinnerCON;
    private javax.swing.JSpinner spinnerDEX;
    private javax.swing.JSpinner spinnerINT;
    private javax.swing.JSpinner spinnerSTR;
    private javax.swing.JSpinner spinnerWIS;
    private javax.swing.JTable tableBasicI;
    private javax.swing.JTextArea textABonds;
    private javax.swing.JTextArea textAFeatsTraits;
    private javax.swing.JTextArea textAFlaws;
    private javax.swing.JTextArea textAIdeals;
    private javax.swing.JTextArea textAPersonality;
    private javax.swing.JTextArea textAreaStory;
    private javax.swing.JTextField textFieldName;
    private javax.swing.JTextPane textPaneSkillsB;
    private javax.swing.JTextPane textPaneSkillsC;
    private javax.swing.JLabel totalCHA;
    private javax.swing.JLabel totalCON;
    private javax.swing.JLabel totalDEX;
    private javax.swing.JLabel totalINT;
    private javax.swing.JLabel totalSTR;
    private javax.swing.JLabel totalWIS;
    // End of variables declaration//GEN-END:variables
}
