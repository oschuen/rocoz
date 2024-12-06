/*
 * Copyright © 2023 Oliver Schünemann (oschuen@users.noreply.github.com)
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
package rocsim.gui.widgets;

import java.awt.Dimension;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public abstract class PaintedImageButton extends JButton {
  private static final long serialVersionUID = 1L;

  public PaintedImageButton() {
    super();
    super.setIcon(new ImageIcon(createImage()));
    super.setPreferredSize(new Dimension(getIcon().getIconWidth(), getIcon().getIconHeight()));
  }

  protected abstract Image createImage();

}
