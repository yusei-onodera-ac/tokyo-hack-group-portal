(function () {
  "use strict";

  var TEMPLATES = {
    SPECIFICATION:
      "# 仕様書: \n\n" +
      "## 概要\n\n\n" +
      "## 背景・目的\n\n\n" +
      "## 要件\n- \n\n" +
      "## 画面・機能仕様\n\n\n" +
      "## 制約事項\n\n",
    DESIGN:
      "# 設計書: \n\n" +
      "## 概要\n\n\n" +
      "## アーキテクチャ\n\n\n" +
      "## データモデル\n\n\n" +
      "## 処理フロー\n\n\n" +
      "## 検討した代替案\n\n",
    MINUTES:
      "# 議事録\n\n" +
      "- 日時: \n" +
      "- 参加者: \n" +
      "- 場所/URL: \n\n" +
      "## アジェンダ\n1. \n\n" +
      "## 決定事項\n\n\n" +
      "## ToDo\n- [ ] \n\n" +
      "## 次回予定\n\n",
    OTHER: "# \n\n## 概要\n\n"
  };

  document.addEventListener("click", function (event) {
    var button = event.target.closest("[data-use-template-for]");
    if (!button) {
      return;
    }

    var categorySelect = document.getElementById(button.getAttribute("data-use-template-for"));
    var contentTextarea = document.getElementById(button.getAttribute("data-template-target"));
    if (!categorySelect || !contentTextarea) {
      return;
    }

    var template = TEMPLATES[categorySelect.value] || TEMPLATES.OTHER;

    if (contentTextarea.value.trim() !== "" && !confirm("入力中の本文をテンプレートで上書きしますか？")) {
      return;
    }

    contentTextarea.value = template;
  });
})();
