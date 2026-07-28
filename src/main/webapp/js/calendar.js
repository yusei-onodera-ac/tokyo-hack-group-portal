(function () {
  "use strict";

  var state = {
    viewMode: "month",
    refDate: new Date(),
    events: [],
    projectId: (window.CALENDAR_CONFIG && window.CALENDAR_CONFIG.projectId) || null
  };

  var WEEKDAY_LABELS = ["日", "月", "火", "水", "木", "金", "土"];

  function pad(value) {
    return String(value).padStart(2, "0");
  }

  function toLocalDateTimeString(date) {
    return date.getFullYear() + "-" + pad(date.getMonth() + 1) + "-" + pad(date.getDate())
        + "T" + pad(date.getHours()) + ":" + pad(date.getMinutes()) + ":00";
  }

  function parseServerDateTime(value) {
    return new Date(value);
  }

  function startOfDay(date) {
    var result = new Date(date);
    result.setHours(0, 0, 0, 0);
    return result;
  }

  function endOfDay(date) {
    var result = new Date(date);
    result.setHours(23, 59, 59, 0);
    return result;
  }

  function addDays(date, amount) {
    var result = new Date(date);
    result.setDate(result.getDate() + amount);
    return result;
  }

  function dateKey(date) {
    return date.getFullYear() + "-" + pad(date.getMonth() + 1) + "-" + pad(date.getDate());
  }

  function getRangeForView() {
    var ref = state.refDate;

    if (state.viewMode === "month") {
      var firstOfMonth = new Date(ref.getFullYear(), ref.getMonth(), 1);
      var gridStart = addDays(firstOfMonth, -firstOfMonth.getDay());
      var gridEnd = addDays(gridStart, 41);
      return { from: startOfDay(gridStart), to: endOfDay(gridEnd) };
    }
    if (state.viewMode === "week") {
      var weekStart = addDays(ref, -ref.getDay());
      return { from: startOfDay(weekStart), to: endOfDay(addDays(weekStart, 6)) };
    }
    if (state.viewMode === "day") {
      return { from: startOfDay(ref), to: endOfDay(ref) };
    }
    return { from: startOfDay(ref), to: endOfDay(addDays(ref, 29)) };
  }

  function loadEvents() {
    var range = getRangeForView();
    var params = new URLSearchParams({
      from: toLocalDateTimeString(range.from),
      to: toLocalDateTimeString(range.to)
    });
    if (state.projectId) {
      params.set("projectId", state.projectId);
    }

    fetch("/calendar/events?" + params.toString())
        .then(function (response) { return response.json(); })
        .then(function (data) {
          state.events = data;
          render();
        });
  }

  function render() {
    updatePeriodLabel();
    var root = document.getElementById("calendar-root");
    root.innerHTML = "";

    if (state.viewMode === "month") {
      renderMonth(root);
    } else {
      renderList(root, getRangeForView(), state.viewMode);
    }
  }

  function updatePeriodLabel() {
    var label = document.getElementById("cal-period-label");
    var ref = state.refDate;

    if (state.viewMode === "month") {
      label.textContent = ref.getFullYear() + "年" + (ref.getMonth() + 1) + "月";
    } else if (state.viewMode === "day") {
      label.textContent = ref.getFullYear() + "年" + (ref.getMonth() + 1) + "月" + ref.getDate() + "日";
    } else {
      var range = getRangeForView();
      label.textContent = formatMonthDay(range.from) + " 〜 " + formatMonthDay(range.to);
    }
  }

  function formatMonthDay(date) {
    return (date.getMonth() + 1) + "/" + date.getDate();
  }

  function eventChipClass(eventType) {
    if (eventType === "MILESTONE") { return "cal-chip-milestone"; }
    if (eventType === "MEETING") { return "cal-chip-meeting"; }
    return "cal-chip-personal";
  }

  function eventBadgeClass(eventType) {
    if (eventType === "MILESTONE") { return "badge-danger"; }
    if (eventType === "MEETING") { return "badge-primary"; }
    return "badge-warning";
  }

  function eventsForDay(dayKey) {
    return state.events.filter(function (event) {
      return event.start.slice(0, 10) === dayKey;
    });
  }

  function renderMonth(root) {
    var ref = state.refDate;
    var firstOfMonth = new Date(ref.getFullYear(), ref.getMonth(), 1);
    var gridStart = addDays(firstOfMonth, -firstOfMonth.getDay());

    var grid = document.createElement("div");
    grid.className = "cal-month-grid";

    WEEKDAY_LABELS.forEach(function (label) {
      var head = document.createElement("div");
      head.className = "cal-month-head";
      head.textContent = label;
      grid.appendChild(head);
    });

    var todayKey = dateKey(new Date());

    for (var i = 0; i < 42; i++) {
      var cellDate = addDays(gridStart, i);
      var key = dateKey(cellDate);

      var cell = document.createElement("div");
      cell.className = "cal-day-cell"
          + (cellDate.getMonth() !== ref.getMonth() ? " is-outside" : "")
          + (key === todayKey ? " is-today" : "");
      cell.dataset.date = key;

      var dayNumber = document.createElement("div");
      dayNumber.className = "cal-day-number";
      dayNumber.textContent = cellDate.getDate();
      cell.appendChild(dayNumber);

      eventsForDay(key).forEach(function (event) {
        cell.appendChild(createChip(event));
      });

      cell.addEventListener("dragover", function (dragEvent) { dragEvent.preventDefault(); });
      cell.addEventListener("drop", onDropOnCell);

      grid.appendChild(cell);
    }

    root.appendChild(grid);
  }

  function createChip(event) {
    var chip = document.createElement("div");
    chip.className = "cal-chip " + eventChipClass(event.eventType);
    chip.textContent = event.title;
    chip.draggable = !!event.editable;
    chip.dataset.eventId = event.id;

    chip.addEventListener("dragstart", function (dragEvent) {
      dragEvent.dataTransfer.setData("text/plain", String(event.id));
    });
    chip.addEventListener("click", function () { openEditModal(event); });

    return chip;
  }

  function onDropOnCell(dropEvent) {
    dropEvent.preventDefault();
    var eventId = dropEvent.dataTransfer.getData("text/plain");
    var targetDateKey = dropEvent.currentTarget.dataset.date;
    var targetEvent = state.events.find(function (item) { return String(item.id) === eventId; });

    if (!targetEvent || !targetEvent.editable) {
      return;
    }

    var originalStart = parseServerDateTime(targetEvent.start);
    var originalEnd = parseServerDateTime(targetEvent.end);
    var targetDate = new Date(targetDateKey + "T00:00:00");
    var dayDiff = Math.round((startOfDay(targetDate).getTime() - startOfDay(originalStart).getTime()) / 86400000);

    if (dayDiff === 0) {
      return;
    }

    var newStart = addDays(originalStart, dayDiff);
    var newEnd = addDays(originalEnd, dayDiff);

    fetch("/calendar/events/" + targetEvent.id + "/reschedule", {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: new URLSearchParams({
        newStart: toLocalDateTimeString(newStart),
        newEnd: toLocalDateTimeString(newEnd)
      })
    }).then(function () { loadEvents(); });
  }

  function renderList(root, range, mode) {
    var list = document.createElement("div");
    list.className = "cal-list";

    var cursor = startOfDay(range.from);
    var rangeEnd = startOfDay(range.to);
    var hasAnyEvent = false;

    while (cursor.getTime() <= rangeEnd.getTime()) {
      var key = dateKey(cursor);
      var dayEvents = eventsForDay(key).sort(function (a, b) { return a.start.localeCompare(b.start); });

      if (dayEvents.length > 0 || mode === "day") {
        hasAnyEvent = hasAnyEvent || dayEvents.length > 0;

        var group = document.createElement("div");
        group.className = "cal-list-group";

        var heading = document.createElement("div");
        heading.className = "cal-list-date";
        heading.textContent = cursor.getFullYear() + "年" + (cursor.getMonth() + 1) + "月" + cursor.getDate()
            + "日（" + WEEKDAY_LABELS[cursor.getDay()] + "）";
        group.appendChild(heading);

        if (dayEvents.length === 0) {
          var emptyLine = document.createElement("div");
          emptyLine.className = "text-muted text-sm";
          emptyLine.textContent = "予定はありません。";
          group.appendChild(emptyLine);
        } else {
          dayEvents.forEach(function (event) {
            group.appendChild(createListRow(event));
          });
        }

        list.appendChild(group);
      }

      cursor = addDays(cursor, 1);
    }

    if (!hasAnyEvent) {
      var emptyState = document.createElement("div");
      emptyState.className = "empty-state card";
      emptyState.textContent = "この期間に予定はありません。";
      list.appendChild(emptyState);
    }

    root.appendChild(list);
  }

  function createListRow(event) {
    var row = document.createElement("div");
    row.className = "cal-list-item";

    var badge = document.createElement("span");
    badge.className = "badge " + eventBadgeClass(event.eventType);
    badge.textContent = event.eventTypeLabel;

    var time = document.createElement("span");
    time.className = "cal-list-time";
    time.textContent = event.allDay ? "終日" : pad(parseServerDateTime(event.start).getHours()) + ":" + pad(parseServerDateTime(event.start).getMinutes());

    var title = document.createElement("span");
    title.className = "cal-list-title";
    title.textContent = event.title;
    title.addEventListener("click", function () { openEditModal(event); });

    row.appendChild(badge);
    row.appendChild(time);
    row.appendChild(title);
    return row;
  }

  function openEditModal(event) {
    if (!event.editable) {
      return;
    }

    document.getElementById("editEventTitle").value = event.title;
    document.getElementById("editEventType").value = event.eventType;
    document.getElementById("editEventStart").value = event.start.slice(0, 16);
    document.getElementById("editEventEnd").value = event.end.slice(0, 16);
    document.getElementById("editEventAllDay").checked = event.allDay;
    document.getElementById("editEventLocation").value = event.location || "";
    document.getElementById("editEventDescription").value = event.description || "";

    document.getElementById("edit-event-form").action = "/calendar/events/" + event.id;
    document.getElementById("delete-event-form").action = "/calendar/events/" + event.id + "/delete";

    document.getElementById("edit-event-modal").classList.add("is-open");
  }

  function setViewMode(mode) {
    state.viewMode = mode;
    document.querySelectorAll("#cal-view-tabs .tabs__link").forEach(function (link) {
      link.classList.toggle("is-active", link.dataset.view === mode);
    });
    loadEvents();
  }

  function shiftRef(direction) {
    if (state.viewMode === "month") {
      state.refDate = new Date(state.refDate.getFullYear(), state.refDate.getMonth() + direction, 1);
    } else if (state.viewMode === "week") {
      state.refDate = addDays(state.refDate, 7 * direction);
    } else if (state.viewMode === "day") {
      state.refDate = addDays(state.refDate, direction);
    } else {
      state.refDate = addDays(state.refDate, 30 * direction);
    }
    loadEvents();
  }

  document.addEventListener("DOMContentLoaded", function () {
    if (!document.getElementById("calendar-root")) {
      return;
    }

    document.querySelectorAll("#cal-view-tabs .tabs__link").forEach(function (link) {
      link.addEventListener("click", function (clickEvent) {
        clickEvent.preventDefault();
        setViewMode(link.dataset.view);
      });
    });

    document.getElementById("cal-prev").addEventListener("click", function () { shiftRef(-1); });
    document.getElementById("cal-next").addEventListener("click", function () { shiftRef(1); });
    document.getElementById("cal-today").addEventListener("click", function () {
      state.refDate = new Date();
      loadEvents();
    });

    loadEvents();
  });
})();
