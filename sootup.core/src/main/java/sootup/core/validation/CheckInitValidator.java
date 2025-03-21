package sootup.core.validation;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997-2020 Raja Vallée-Rai, Linghui Luo, Markus Schmidt
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.util.List;
import sootup.core.model.Body;
import sootup.core.views.View;

public class CheckInitValidator implements BodyValidator {

  @Override
  public List<ValidationException> validate(Body body, View view) {

    // TODO: #535 implement validator
    //  check code copied from old soot
    /*
     * ExceptionalUnitGraph g = new ExceptionalUnitGraph(body, ThrowAnalysisFactory.checkInitThrowAnalysis(), false);
     *
     * InitAnalysis analysis = new InitAnalysis(g); for (Unit s : body.getUnits()) { FlowSet<Local> init =
     * analysis.getFlowBefore(s); for (ValueBox vBox : s.getUseBoxes()) { Value v = vBox.getValue(); if (v instanceof Local)
     * { Local l = (Local) v; if (!init.contains(l)) { throw new ValidationException(s,
     * "Local variable $1 is not definitively defined at this point".replace("$1", l.getName()), "Warning: Local variable " +
     * l + " not definitely defined at " + s + " in " + body.getMethod(), false); } } } }
     */
    return null;
  }

  @Override
  public boolean isBasicValidator() {
    return false;
  }
}
