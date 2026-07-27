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

  document.addEventListener("keydown", function (event) {
    if (event.key === "Escape") {
      document.querySelectorAll(".modal-overlay.is-open").forEach(function (modal) {
        modal.classList.remove("is-open");
      });
      toggleSidebar(false);
    }
  });
})();
