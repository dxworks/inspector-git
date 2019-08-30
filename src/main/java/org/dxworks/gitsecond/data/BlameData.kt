package org.dxworks.gitsecond.data

data class BlameData(var changeData: ChangeData, var blamedLines: List<BlamedLine>) {
}