package com.intellij.ideolog.highlighting.settings

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.EditorTextField
import com.intellij.ui.JBIntSpinner
import net.miginfocom.swing.MigLayout
import org.intellij.lang.regexp.RegExpFileType
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.lang.IllegalArgumentException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class LogParsingPatternSettingsDialog(val item: LogParsingPattern) : DialogWrapper(null, true, IdeModalityType.PROJECT) {
  private var myNameText: EditorTextField? = null
  private var myParsingPatternText: EditorTextField? = null
  private var myLineStartPatternText: EditorTextField? = null
  private var myTimePatternText: EditorTextField? = null

  private var myTimeColumnId: JBIntSpinner? = null
  private var mySeverityColumnId: JBIntSpinner? = null
  private var myCategoryColumnId: JBIntSpinner? = null
  private var myOnlyFirstLineRegexCheckbox: JCheckBox? = null

  init {
    init()
    initValidation()
  }

  override fun createCenterPanel(): JComponent? {
    val panel = JPanel(GridBagLayout())
    val constraints = GridBagConstraints().apply {
      gridx = 0
      gridy = 0
      fill = GridBagConstraints.HORIZONTAL
      anchor = GridBagConstraints.WEST
    }

    val labels = arrayOf("Name: ", "Message pattern: ", "Message start pattern: ", "Time format: ", "Time capture group: ", "Severity capture group: ", "Category capture group: ")


    labels.forEach {
      panel.add(JLabel(it), constraints)
      constraints.gridy++
    }

    constraints.apply {
      anchor = GridBagConstraints.SOUTHEAST
      gridx = 1
      gridy = 0
    }

    val nameText = EditorTextField(item.name)
    myNameText = nameText
    panel.add(nameText, constraints)
    constraints.gridy++

    val patternText = EditorTextField(item.pattern, ProjectManager.getInstance().defaultProject, RegExpFileType.INSTANCE)
    myParsingPatternText = patternText
    panel.add(patternText, constraints)
    constraints.gridy++

    val linePatternText = EditorTextField(item.lineStartPattern, ProjectManager.getInstance().defaultProject, RegExpFileType.INSTANCE)
    myLineStartPatternText = linePatternText
    panel.add(linePatternText, constraints)
    constraints.gridy++

    val timeFormatText = EditorTextField(item.timePattern)
    myTimePatternText = timeFormatText
    panel.add(timeFormatText, constraints)
    constraints.gridy++

    val timeSpinner = JBIntSpinner(item.timeColumnId + 1, 0, 100)
    myTimeColumnId = timeSpinner
    panel.add(timeSpinner, constraints)
    constraints.gridy++

    val severitySpinner = JBIntSpinner(item.severityColumnId + 1, 0, 100)
    mySeverityColumnId = severitySpinner
    panel.add(severitySpinner, constraints)
    constraints.gridy++

    val categorySpinner = JBIntSpinner(item.categoryColumnId + 1, 0, 100)
    myCategoryColumnId = categorySpinner
    panel.add(categorySpinner, constraints)
    constraints.gridy++

    val firstLineRegexCheckbox = JCheckBox("Apply message pattern to all message lines", item.regexMatchFullEvent)
    myOnlyFirstLineRegexCheckbox = firstLineRegexCheckbox
    constraints.gridx = 0
    constraints.gridwidth = 2
    panel.add(firstLineRegexCheckbox, constraints)

    return panel
  }

  override fun doHelpAction() {
    BrowserUtil.browse("https://github.com/JetBrains/ideolog/wiki/Custom-Log-Formats")
  }

  override fun doOKAction() {
    myNameText?.let { item.name = it.text }
    myParsingPatternText?.let { item.pattern = it.text }
    myLineStartPatternText?.let { item.lineStartPattern = it.text }
    myTimePatternText?.let { item.timePattern = it.text }

    myTimeColumnId?.let { item.timeColumnId = it.number - 1 }
    mySeverityColumnId?.let { item.severityColumnId = it.number - 1 }
    myCategoryColumnId?.let { item.categoryColumnId = it.number - 1 }

    myOnlyFirstLineRegexCheckbox?.let { item.regexMatchFullEvent = it.isSelected }

    super.doOKAction()
  }

  override fun doValidateAll(): MutableList<ValidationInfo> {
    val results = ArrayList<ValidationInfo>()

    try {
      myParsingPatternText?.let { Pattern.compile(it.text) }
    } catch(e : PatternSyntaxException) {
      results.add(ValidationInfo(e.localizedMessage, myParsingPatternText))
    }

    try {
      myLineStartPatternText?.let { Pattern.compile(it.text) }
    } catch(e : PatternSyntaxException) {
      results.add(ValidationInfo(e.localizedMessage, myLineStartPatternText))
    }

    try {
      myTimePatternText?.let { SimpleDateFormat(it.text) }
    } catch(e : IllegalArgumentException) {
      results.add(ValidationInfo(e.localizedMessage, myTimePatternText))
    }

    return results
  }
}
