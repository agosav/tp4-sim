/* eslint-disable react/prop-types */
import React from "react";

const Results = ({ cantSillas, prom }) => {
  return (
    <div className="card">
      <h4>El promedio de recaudación diaria de la peluquería es de: ${prom}</h4>
      <br />
      <h4>
        La cantidad de sillas son necesarias para que en ningún momento se
        encuentre un cliente de pie es de: {cantSillas} sillas
      </h4>
    </div>
  );
};

export { Results };
