/* eslint-disable react/prop-types */
import React, { useEffect, useState } from "react";
import { Modal, Table, Button } from "react-bootstrap";
import { colasServices } from "../services/colas.service";

export default function RKModal({ show, handleClose }) {
  const [rk, setRk] = useState([]);

  useEffect(() => {
    const getRungeKutta = async () => {
      setRk(await colasServices.getRK());
    };
    getRungeKutta();
  }, []);

  useEffect(() => {
    console.log("RK: ", rk);
  }, [rk]);

  return (
    <Modal show={show} onHide={handleClose} size="xl">
      <Modal.Header closeButton>
        <Modal.Title>Tabla Runge-Kutta</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        {rk.length > 0 && (
          <Table striped bordered hover>
            <thead>
              <tr>
                <th>x</th>
                <th>C</th>
                <th>k1</th>
                <th>k2</th>
                <th>k3</th>
                <th>k4</th>
                <th>x(i+1)</th>
                <th>C(i+1)</th>
              </tr>
            </thead>
            <tbody>
              {rk.map((row, index) => (
                <tr key={index}>
                  {row.map((value, subIndex) => (
                    <td key={subIndex}>{value}</td>
                  ))}
                </tr>
              ))}
            </tbody>
          </Table>
        )}
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={handleClose}>
          Cerrar
        </Button>
      </Modal.Footer>
    </Modal>
  );
}
