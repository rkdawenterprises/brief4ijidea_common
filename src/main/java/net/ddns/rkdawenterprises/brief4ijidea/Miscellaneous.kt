/*
 * Copyright (c) 2019-2022 RKDAW Enterprises and Ralph Williamson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("FunctionName",
               "LocalVariableName",
               "PrivatePropertyName",
               "HardCodedStringLiteral",
               "unused",
               "RedundantSemicolon",
               "UsePropertyAccessSyntax",
               "KDocUnresolvedReference")

package net.ddns.rkdawenterprises.brief4ijidea

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.impl.ActionManagerImpl
import com.intellij.openapi.command.undo.UndoManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.project.Project
import net.ddns.rkdawenterprises.brief4ijidea.compatibility.get_editor_content_visible_area
import net.ddns.rkdawenterprises.brief4ijidea.compatibility.perform_action_dumb_aware_with_callbacks
import java.util.*

fun to_nearest_visual_line_base(editor: Editor,
                                y: Int): Int
{
    val transformed = editor.visualLineToY(editor.yToVisualLine(y))
    return if(y > transformed && y < transformed + editor.lineHeight) transformed else y
}

/**
 * Scrolls the given editor the given number of lines up or down.
 *
 * @param editor The editor to scroll.
 * @param lines Negative value scrolls to a decreasing line number. Positive value scrolls to an increasing line number.
 */
fun scroll_lines(editor: Editor,
                 lines: Int)
{
    val lineHeight: Int = editor.lineHeight
    val visibleArea = get_editor_content_visible_area(editor)
    val y = visibleArea.y - (lineHeight * lines);
    val scroll_offset = to_nearest_visual_line_base(editor,
                                                    y);
    editor.scrollingModel
        .scrollVertically(scroll_offset);
}

fun has_selection(editor: Editor): Boolean
{
    return editor.selectionModel
        .hasSelection();
}

fun capitalize_character_at_index(string: String,
                                  index: Int): String
{
    return string.substring(0,
                            index) +
            string.substring(index,
                             index + 1)
                .uppercase(Locale.getDefault()) +
            string.substring(index + 1)
}

fun do_action(action_ID: String,
              an_action_event: AnActionEvent)
{
    val action_manager_ex = ActionManagerImpl.getInstanceEx();
    val action = action_manager_ex.getAction(action_ID);
    perform_action_dumb_aware_with_callbacks(action,
                                             an_action_event);
}

fun do_action(action_ID: String,
              an_action_event: AnActionEvent,
              an_action: AnAction)
{
    if(!should_promote(an_action,
                       an_action_event.dataContext)) return;
    val action_manager_ex = ActionManagerImpl.getInstanceEx();
    val action = action_manager_ex.getAction(action_ID);

    perform_action_dumb_aware_with_callbacks(action,
                                             an_action_event);
}

fun get_undo_manager(project: Project?): UndoManager
{
    return if((project != null) && !project.isDefault) UndoManager.getInstance(project) else UndoManager.getGlobalInstance();
}

fun stop_all_marking_modes(editor: Editor,
                           remove_selection: Boolean)
{
    Marking_component.stop_marking_mode(editor,
                                        remove_selection)
    Line_marking_component.stop_line_marking_mode(editor,
                                                  remove_selection)
    Column_marking_component.stop_column_marking_mode(editor,
                                                      remove_selection)
    State_component.status_bar_message(null)
    if(remove_selection)
    {
        if(has_selection(editor))
        {
            editor.caretModel
                .removeSecondaryCarets()
            editor.selectionModel
                .removeSelection()
        }
    }
}

fun stop_all_marking_modes(editor: Editor)
{
    stop_all_marking_modes(editor,
                           true)
}

fun validate_position(editor: Editor,
                      position: LogicalPosition): LogicalPosition
{
    return editor.offsetToLogicalPosition(editor.logicalPositionToOffset(position))
}

fun editor_gained_focus(editor: Editor)
{
    stop_all_marking_modes(editor,
                           false)
}

fun editor_lost_focus(editor: Editor)
{
    stop_all_marking_modes(editor,
                           false)
}

fun toggle_marking_mode(editor: Editor)
{
    Line_marking_component.stop_line_marking_mode(editor,
                                                  true)
    Column_marking_component.stop_column_marking_mode(editor,
                                                      true)
    Marking_component.toggle_marking_mode(editor)
}

fun toggle_line_marking_mode(editor: Editor)
{
    Marking_component.stop_marking_mode(editor,
                                        true)
    Column_marking_component.stop_column_marking_mode(editor,
                                                      true)
    Line_marking_component.toggle_line_marking_mode(editor)
}

fun toggle_column_marking_mode(editor: Editor)
{
    Marking_component.stop_marking_mode(editor,
                                        true)
    Line_marking_component.stop_line_marking_mode(editor,
                                                  true)
    Column_marking_component.toggle_column_marking_mode(editor)
}

fun get_bottom_of_window_line_number(editor: Editor): Int
{
    val visible_area = get_editor_content_visible_area(editor);
    val max_Y: Int = visible_area.y + visible_area.height - editor.lineHeight;
    var visible_area_bottom_line_number = editor.yToVisualLine(max_Y);
    if(visible_area_bottom_line_number > 0 &&
        max_Y < editor.visualLineToY(visible_area_bottom_line_number) &&
        visible_area.y <= editor.visualLineToY(visible_area_bottom_line_number - 1))
    {
        visible_area_bottom_line_number--;
    }

    return visible_area_bottom_line_number;
}

fun get_top_of_window_line_number(editor: Editor): Int
{
    val visible_area = get_editor_content_visible_area(editor);
    var visible_area_top_line_number = editor.yToVisualLine(visible_area.y);
    if(visible_area.y > editor.visualLineToY(visible_area_top_line_number) &&
        visible_area.y + visible_area.height > editor.visualLineToY(visible_area_top_line_number + 1)
    )
    {
        visible_area_top_line_number++;
    }

    return visible_area_top_line_number;
}
