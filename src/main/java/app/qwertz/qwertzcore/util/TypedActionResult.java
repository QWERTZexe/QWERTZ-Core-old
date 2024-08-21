/*
        Copyright (C) 2024 QWERTZ_EXE

        This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License
        as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

        This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
        without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
        See the GNU Lesser General Public License for more details.

        You should have received a copy of the GNU Lesser General Public License along with this program.
        If not, see <http://www.gnu.org/licenses/>.
*/

package app.qwertz.qwertzcore.util;

import net.minecraft.util.ActionResult;

public class TypedActionResult<T> {
    private final ActionResult result;
    private final T value;

    public TypedActionResult(ActionResult result, T value) {
        this.result = result;
        this.value = value;
    }

    public ActionResult getResult() {
        return result;
    }

    public T getValue() {
        return value;
    }

    public static <T> TypedActionResult<T> success(T value) {
        return new TypedActionResult<>(ActionResult.SUCCESS, value);
    }

    public static <T> TypedActionResult<T> pass(T value) {
        return new TypedActionResult<>(ActionResult.PASS, value);
    }

    public static <T> TypedActionResult<T> fail(T value) {
        return new TypedActionResult<>(ActionResult.FAIL, value);
    }
}