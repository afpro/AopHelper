package net.afpro.idea.aophelper.lancet

import javax.swing.table.AbstractTableModel

class LancetTabModel(val columnNames: List<String>, val rowData: List<LancetElement>) : AbstractTableModel() {

    override fun getRowCount(): Int {
        return rowData.size
    }

    override fun getColumnCount(): Int {
        return columnNames.size
    }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): String? {
        val element = rowData[rowIndex]
        if (columnIndex == 0) {
            return element.lancetClass
        } else if (columnIndex == 1) {
            return element.targetClass
        } else {
            return element.targetMethod
        }
    }

    override fun getColumnName(column: Int): String {
        return columnNames[column]
    }
}