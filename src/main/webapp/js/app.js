(function () {
  "use strict";

  function toggleSidebar(open) {
    var sidebar = document.querySelector("[data-sidebar]");
    var backdrop = document.querySelector("[data-sidebar-backdrop]");
    if (!sidebar || !backdrop) {
      return;
    }
    sidebar.classList.toggle("is-open", open);
    backdrop.classList.toggle("is-open", open);
  }

  document.addEventListener("click", function (event) {
    var opener = event.target.closest("[data-sidebar-open]");
    if (opener) {
      toggleSidebar(true);
      return;
    }

    var closer = event.target.closest("[data-sidebar-close]");
    if (closer) {
      toggleSidebar(false);
      return;
    }

    var modalOpener = event.target.closest("[data-modal-open]");
    if (modalOpener) {
      var targetModal = document.getElementById(modalOpener.getAttribute("data-modal-open"));
      if (targetModal) {
        targetModal.classList.add("is-open");
      }
      return;
    }

    var modalCloser = event.target.closest("[data-modal-close]");
    if (modalCloser) {
      var openModal = modalCloser.closest(".modal-overlay");
      if (openModal) {
        openModal.classList.remove("is-open");
      }
      return;
    }

    if (event.target.classList && event.target.classList.contains("modal-overlay")) {
      event.target.classList.remove("is-open");
    }
  });

  // 長文が実際に3行を超えて省略されている場合のみ「続きを読む」ボタンを表示する。
  document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll(".clamp-content").forEach(function (content) {
      var toggle = content.nextElementSibling;
      if (!toggle || !toggle.classList.contains("clamp-toggle")) {
        return;
      }
      if (content.scrollHeight <= content.clientHeight + 1) {
        toggle.style.display = "none";
      }
    });
  });

  document.addEventListener("keydown", function (event) {
    if (event.key === "Escape") {
      document.querySelectorAll(".modal-overlay.is-open").forEach(function (modal) {
        modal.classList.remove("is-open");
      });
      toggleSidebar(false);
    }
  });

  // フォームの二重送信（連打・複数回のEnter等）による重複登録を防止する。
  // 送信ボタンを即座に無効化し、ページ遷移（redirect後の別画面表示）までクリックを受け付けない。
  // onsubmit="return confirm(...)" でキャンセルされた場合（event.defaultPrevented）は無効化しない。
  document.addEventListener("submit", function (event) {
    var form = event.target;
    if (!(form instanceof HTMLFormElement) || event.defaultPrevented) {
      return;
    }
    form.querySelectorAll('button[type="submit"], input[type="submit"]').forEach(function (button) {
      button.disabled = true;
    });
  });
})();
