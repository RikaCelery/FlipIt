package org.celery.flipit

import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AdsClick
import androidx.compose.material.icons.rounded.AllOut
import androidx.compose.material.icons.rounded.ArrowOutward
import androidx.compose.material.icons.rounded.AssistWalker
import androidx.compose.material.icons.rounded.AssistantDirection
import androidx.compose.material.icons.rounded.BackupTable
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Lan
import androidx.compose.material.icons.rounded.LastPage
import androidx.compose.material.icons.rounded.Mail
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.Padding
import androidx.compose.material.icons.rounded.Radio
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SafetyCheck
import androidx.compose.material.icons.rounded.Sailing
import androidx.compose.material.icons.rounded.Satellite
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.celery.flipit.ui.theme.FlipItTheme

data class AAA(
    val id: Int, var icon: ImageVector, val display: MutableState<Boolean>
) {
    constructor(id: Int, icon: ImageVector, init: Boolean) : this(id, icon, mutableStateOf(init))

    fun new(): AAA {
        return AAA(id, icon, display.value)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val scope = CoroutineScope(SupervisorJob())
        setContent {
            FlipItTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Column(
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        var rows by remember {
                            mutableStateOf(0)
                        }
                        var cols by remember {
                            mutableStateOf(0)
                        }
                        var rowsS by remember {
                            mutableStateOf("")
                        }
                        var colsS by remember {
                            mutableStateOf("")
                        }
                        val tables = listOf(
                            Icons.Rounded.Sailing,
                            Icons.Rounded.SafetyCheck,
                            Icons.Rounded.Badge,
                            Icons.Rounded.Lan,
                            Icons.Rounded.Padding,
                            Icons.Rounded.Mail,
                            Icons.Rounded.Map,
                            Icons.Rounded.Call,
                            Icons.Rounded.Settings,
                            Icons.Rounded.LastPage,
                            Icons.Rounded.ArrowOutward,
                            Icons.Rounded.Satellite,
                            Icons.Rounded.Radio,
                            Icons.Rounded.BackupTable,
                            Icons.Rounded.AdsClick,
                            Icons.Rounded.AllOut,
                            Icons.Rounded.AssistWalker,
                            Icons.Rounded.AssistantDirection,
                        ).mapIndexed { index, imageVector ->
                            AAA(index + 1, imageVector, false)
                        }
                        val list = remember {
                            mutableStateListOf<AAA>()
                        }
                        val trys = remember {
                            mutableStateListOf<Pair<Triple<Point, Point, Long>, Boolean>>()
                        }
                        var lastFlip: Pair<Int, Int>? = remember {
                            null
                        }
                        var lastFlipTime: Long? = remember {
                            null
                        }
                        var blockInput by remember {
                            mutableStateOf(false)
                        }
                        val selections = remember {
                            mutableStateListOf<Int>()
                        }
                        val errors = remember {
                            mutableStateListOf<Int>()
                        }
                        val oks = remember {
                            mutableStateListOf<Int>()
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextField(value = rowsS.toString(), onValueChange = { res ->
                                rowsS = res
                            }, label = { Text(text = "rows") }, modifier = Modifier.weight(1f))
                            TextField(value = colsS.toString(), onValueChange = { res ->
                                colsS = res
                            }, label = { Text("columns") }, modifier = Modifier.weight(1f))
                            Button(onClick = {
                                val tr = rowsS.toIntOrNull()?.coerceAtLeast(1) ?: return@Button
                                val tc = colsS.toIntOrNull()?.coerceAtLeast(1) ?: return@Button

                                Log.d("AAA", "tr tc: $tr $tc")
                                if (tr * tc > tables.size * 2) {
                                    runOnUiThread {
                                        Toast.makeText(
                                            applicationContext,
                                            "太长了,不能超过${tables.size}！",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    return@Button
                                }
                                if ((tr * tc) % 2 != 0) {
                                    runOnUiThread {
                                        Toast.makeText(
                                            applicationContext, "不是偶数！", Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    return@Button
                                }

                                oks.clear()
                                selections.clear()
                                errors.clear()
                                lastFlip = null
                                list.clear()
                                for (i in 0 until (tr * tc).div(2)) {
                                    list.add(tables[i].new())
                                    list.add(tables[i].new())
                                }
                                list.shuffle()
                                trys.clear()
                                rows = tr
                                cols = tc
                            }) {
                                Icon(
                                    imageVector = Icons.Rounded.Refresh,
                                    contentDescription = "reload"
                                )
                                Text(text = "Reload")
                            }
                        }

                        GameView(list, rows, cols, selections, errors, oks) { r, c ->
                            Log.d("AAAA", "GameView ok size: ${oks.size}")
                            if (blockInput || oks.contains((r to c).hashCode())) return@GameView
                            val TAG = "AAA"
                            Log.e(TAG, "onCreate: $r $c ${list[r * (cols) + c]}")
                            list[r * (cols) + c].display.value = true
                            Log.e(TAG, "onCreate: $r $c ${list[r * (cols) + c]}")
                            selections.add((r to c).hashCode())
                            if (lastFlip == null) {
                                lastFlip = r to c
                                lastFlipTime = System.currentTimeMillis()
                                blockInput = false
                            } else {
                                if (lastFlip == r to c) return@GameView
                                blockInput = true
                                scope.launch {
                                    if (list[lastFlip!!.first * (cols) + lastFlip!!.second].id == list[r * (cols) + c].id) {
                                        oks.add(lastFlip!!.hashCode())
                                        oks.add((r to c).hashCode())
                                        trys.add(
                                            Triple(
                                                Point(r, c),
                                                Point(lastFlip!!.first, lastFlip!!.second),
                                                lastFlipTime!!
                                            ) to true
                                        )
                                        if (oks.size == rows * cols) {
                                            runOnUiThread {
                                                runOnUiThread {
                                                    Toast.makeText(
                                                        applicationContext,
                                                        "爹你好厉害啊！",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        }
                                    } else {
                                        trys.add(
                                            Triple(
                                                Point(r, c),
                                                Point(lastFlip!!.first, lastFlip!!.second),
                                                lastFlipTime!!
                                            ) to false
                                        )
                                        errors.add(lastFlip!!.hashCode())
                                        errors.add((r to c).hashCode())
                                        delay(200)
                                        list[lastFlip!!.first * cols + lastFlip!!.second].display.value =
                                            false
                                        list[r * cols + c].display.value = false
                                        errors.clear()
                                    }
                                    selections.clear()
                                    lastFlip = null
                                    lastFlipTime = null
                                    blockInput = false
                                }
                            }
                        }
                        var cur by remember {
                            mutableStateOf(-1)
                        }
                        LazyColumn {
                            itemsIndexed(trys) { index, item ->
                                if ((oks.size == rows * cols))
                                    if (cur == index) {
                                        Spacer(Modifier.width(3.dp))
                                    }
                                MyButton(
                                    onClick = {
                                        cur = index
                                        selections.clear()
                                        selections.add((item.first.first.x to item.first.first.y).hashCode())
                                        selections.add((item.first.second.x to item.first.second.y).hashCode())
                                    },
                                    modifier = Modifier
                                        .background(Color(0x03000000))
                                        .apply {

                                        }
                                        .fillMaxWidth(1f),
                                    color = if (item.second) Color(0xFF9DF07A)
                                    else Color(0xFFFD5757)
                                ) {

                                    Text(
                                        "[${index.plus(1)}] (${item.first.first})-(${item.first.second}), thinking ${item.first.third} ms",
                                        overflow = TextOverflow.Ellipsis, modifier = Modifier
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyButton(
    onClick: () -> Unit,
    shape: Shape = ButtonDefaults.shape,
    color: Color,
    modifier: Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Surface(onClick = onClick, shape = shape, modifier = modifier, color = color) {
        ProvideTextStyle(value = MaterialTheme.typography.labelLarge) {
            Row(
                Modifier.defaultMinSize(
                    minWidth = ButtonDefaults.MinWidth, minHeight = ButtonDefaults.MinHeight
                ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }
    }
}

@Composable
private fun GameView(
    list: MutableList<AAA>,
    rows: Int,
    cols: Int,
    heightSelections: MutableList<Int>,
    heightLightErrors: MutableList<Int>,
    ok: MutableList<Int>,
    onClick: (Int, Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(rows) { rowIndex ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(cols) { columnIndex ->
                    Log.d(
                        "AAA",
                        "GameView: $rowIndex($rows) $columnIndex($cols) ${rowIndex * (cols) + columnIndex}:${list.size}"
                    )
                    val fliped = (list.getOrNull(rowIndex * (cols) + columnIndex)!!.display.value)
                    Log.d("AAA", "GameView: $rowIndex $columnIndex $fliped")

                    MyButton(
                        onClick = { onClick(rowIndex, columnIndex) },
                        shape = RoundedCornerShape(8.dp),
                        color = when {
                            heightLightErrors.contains((rowIndex to columnIndex).hashCode()) -> Color.Red
                            heightSelections.contains((rowIndex to columnIndex).hashCode()) -> Color.Cyan
                            ok.contains((rowIndex to columnIndex).hashCode()) -> Color.Green
                            else -> MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.size(50.dp)
                    ) {
                        if (fliped) Icon(list[rowIndex * (cols) + columnIndex].icon, null)
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {

}