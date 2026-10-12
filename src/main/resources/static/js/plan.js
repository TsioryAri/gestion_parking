// Plan du parking : un clic sur un statut de la légende met en évidence les places correspondantes
document.addEventListener("DOMContentLoaded", function () {
    var actifs = new Set();
    document.querySelectorAll(".legend-item").forEach(function (item) {
        item.addEventListener("click", function () {
            var statut = item.getAttribute("data-statut");
            if (actifs.has(statut)) {
                actifs.delete(statut);
                item.classList.remove("active");
            } else {
                actifs.add(statut);
                item.classList.add("active");
            }
            document.querySelectorAll(".place").forEach(function (p) {
                var visible = actifs.size === 0 || actifs.has(p.getAttribute("data-statut"));
                p.classList.toggle("dimmed", !visible);
            });
        });
    });
});
